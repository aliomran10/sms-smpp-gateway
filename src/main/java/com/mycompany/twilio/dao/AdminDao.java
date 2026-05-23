package com.mycompany.twilio.dao;

import com.mycompany.twilio.model.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Ali
 */
/**
 * Data-access object for all administrator operations.
 *
 * Covers: - list / view / add / edit / delete customers - per-customer SMS
 * statistics - platform-wide summary statistics
 */
public class AdminDao {

    private final Connection con;

    public AdminDao(Connection con) {
        this.con = con;
    }

    // ── Customer CRUD ─────────────────────────────────────────────────────────
    public List<User> getAllCustomers() {
        List<User> list = new ArrayList<>();
        String sql = """
                SELECT user_id, full_name, birthday, job,
                       email, msisdn, physical_address,
                       twilio_account_sid, twilio_auth_token,
                       twilio_sender_id
                FROM   users
                WHERE  is_admin = FALSE
                ORDER  BY created_at DESC
                """;
        try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapUser(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public User getCustomerById(int userId) {
        String sql = """
                SELECT user_id, full_name, birthday, job,
                       email, msisdn, physical_address,
                       twilio_account_sid, twilio_auth_token,
                       twilio_sender_id
                FROM   users
                WHERE  user_id = ?
                """;
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Inserts a brand-new customer created by the admin. Account is immediately
     * verified — no OTP flow needed.
     *
     * @return generated user_id, or -1 on failure
     */
    public int addCustomer(User user, String rawPassword) {
        String sql = """
                INSERT INTO users
                    (full_name, birthday, job, email, password_hash,
                     is_admin, msisdn, physical_address,
                     twilio_account_sid, twilio_auth_token,
                     twilio_sender_id, created_at, is_verified)
                VALUES (?, ?, ?, ?, ?, FALSE, ?, ?, ?, ?, ?,
                        CURRENT_TIMESTAMP, TRUE)
                RETURNING user_id
                """;
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, user.getFullName());
            ps.setDate(2, user.getBirthday() != null && !user.getBirthday().isBlank()
                    ? java.sql.Date.valueOf(user.getBirthday())
                    : null);
            ps.setString(3, user.getJob());
            ps.setString(4, user.getEmail());
            ps.setString(5, rawPassword);
            ps.setString(6, user.getMsisdn());
            ps.setString(7, user.getPhysicalAddress());
            ps.setString(8, user.getTwilioAccountSid());
            ps.setString(9, user.getTwilioAuthToken());
            ps.setString(10, user.getTwilioSenderId());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean updateCustomer(User user) {
        String sql = """
                UPDATE users
                SET    full_name          = ?,
                       birthday           = ?,
                       job                = ?,
                       email              = ?,
                       msisdn             = ?,
                       physical_address   = ?,
                       twilio_account_sid = ?,
                       twilio_sender_id   = ?
                WHERE  user_id = ?
                """;
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, user.getFullName());
            ps.setDate(2, user.getBirthday() != null && !user.getBirthday().isBlank()
                    ? java.sql.Date.valueOf(user.getBirthday())
                    : null);
            ps.setString(3, user.getJob());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getMsisdn());
            ps.setString(6, user.getPhysicalAddress());
            ps.setString(7, user.getTwilioAccountSid());
            ps.setString(8, user.getTwilioSenderId());
            ps.setInt(9, user.getUserId());
            int rows = ps.executeUpdate();
            if (user.getTwilioAuthToken() != null
                    && !user.getTwilioAuthToken().isBlank()) {
                updateAuthToken(user.getUserId(), user.getTwilioAuthToken());
            }
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteCustomer(int userId) {
        String sql = "DELETE FROM users WHERE user_id = ? AND is_admin = FALSE";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // ── Statistics ────────────────────────────────────────────────────────────
    /**
     * Returns per-customer SMS stats with sentPct and receivedPct
     * pre-calculated so JSPs need no EL arithmetic.
     */
    public List<CustomerStats> getAllCustomerStats() {
        List<CustomerStats> list = new ArrayList<>();
        String sql = """
                SELECT  u.user_id,
                        u.full_name,
                        u.email,
                        u.msisdn,
                        COUNT(CASE WHEN m.sender_no    = u.msisdn THEN 1 END) AS total_sent,
                        COUNT(CASE WHEN m.recipient_no = u.msisdn THEN 1 END) AS total_received,
                        TO_CHAR(MAX(m.sent_at), 'YYYY-MM-DD HH24:MI')         AS last_activity
                FROM    users u
                LEFT JOIN messages m
                       ON m.msisdn = u.msisdn
                          OR m.recipient_no = u.msisdn
                WHERE   u.is_admin = FALSE
                GROUP   BY u.user_id, u.full_name, u.email, u.msisdn
                ORDER   BY total_sent DESC
                """;
        try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new CustomerStats(
                        rs.getInt("user_id"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("msisdn"),
                        rs.getInt("total_sent"),
                        rs.getInt("total_received"),
                        rs.getString("last_activity")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Find the maximum sent count for bar scaling
        int maxSent = list.stream()
                .mapToInt(CustomerStats::getTotalSent)
                .max()
                .orElse(1);

        // Pre-calculate percentages on every row
        list.forEach(s -> s.calculatePcts(maxSent));

        return list;
    }

    public CustomerStats getCustomerStats(int userId) {
        String sql = """
                SELECT  u.user_id,
                        u.full_name,
                        u.email,
                        u.msisdn,
                        COUNT(CASE WHEN m.sender_no    = u.msisdn THEN 1 END) AS total_sent,
                        COUNT(CASE WHEN m.recipient_no = u.msisdn THEN 1 END) AS total_received,
                        TO_CHAR(MAX(m.sent_at), 'YYYY-MM-DD HH24:MI')         AS last_activity
                FROM    users u
                LEFT JOIN messages m
                       ON m.msisdn = u.msisdn
                          OR m.recipient_no = u.msisdn
                WHERE   u.user_id = ?
                GROUP   BY u.user_id, u.full_name, u.email, u.msisdn
                """;
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    CustomerStats s = new CustomerStats(
                            rs.getInt("user_id"),
                            rs.getString("full_name"),
                            rs.getString("email"),
                            rs.getString("msisdn"),
                            rs.getInt("total_sent"),
                            rs.getInt("total_received"),
                            rs.getString("last_activity"));
                    // For single-customer view pct is relative to their own total
                    s.calculatePcts(Math.max(s.getTotalSent(), s.getTotalReceived()));
                    return s;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Platform-wide summary: [totalCustomers, totalMessages, totalSentToday]
     */
    public int[] getPlatformSummary() {
        int[] out = { 0, 0, 0 };
        try {
            try (Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery(
                            "SELECT COUNT(*) FROM users WHERE is_admin = FALSE")) {
                if (rs.next()) {
                    out[0] = rs.getInt(1);
                }
            }
            try (Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM messages")) {
                if (rs.next()) {
                    out[1] = rs.getInt(1);
                }
            }
            try (Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery(
                            "SELECT COUNT(*) FROM messages WHERE sent_at::date = CURRENT_DATE")) {
                if (rs.next()) {
                    out[2] = rs.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return out;
    }

    // ── Private helpers ───────────────────────────────────────────────────────
    private User mapUser(ResultSet rs) throws Exception {
        User u = new User();
        u.setUserId(rs.getInt("user_id"));
        u.setFullName(rs.getString("full_name"));
        java.sql.Date bd = rs.getDate("birthday");
        u.setBirthday(bd != null ? bd.toString() : "");
        u.setJob(rs.getString("job"));
        u.setEmail(rs.getString("email"));
        u.setMsisdn(rs.getString("msisdn"));
        u.setPhysicalAddress(rs.getString("physical_address"));
        u.setTwilioAccountSid(rs.getString("twilio_account_sid"));
        u.setTwilioAuthToken(rs.getString("twilio_auth_token"));
        u.setTwilioSenderId(rs.getString("twilio_sender_id"));
        return u;
    }

    private void updateAuthToken(int userId, String token) {
        String sql = "UPDATE users SET twilio_auth_token = ? WHERE user_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, token);
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
