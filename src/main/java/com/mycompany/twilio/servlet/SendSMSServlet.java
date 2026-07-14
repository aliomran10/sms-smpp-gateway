package com.mycompany.twilio.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Duration;
import java.time.Instant;

import org.jsmpp.session.BindParameter;
import org.jsmpp.bean.BindType;
import org.jsmpp.bean.ESMClass;
import org.jsmpp.bean.GeneralDataCoding;
import org.jsmpp.bean.NumberingPlanIndicator;
import org.jsmpp.bean.RegisteredDelivery;
import org.jsmpp.bean.SMSCDeliveryReceipt;
import org.jsmpp.bean.TypeOfNumber;
import org.jsmpp.session.SMPPSession;

import com.mycompany.twilio.util.FailedMessageResender;

@WebServlet("/SendSMSServlet")
public class SendSMSServlet extends HttpServlet {

    private static final String SMPP_HOST = "127.0.0.1";
    private static final int SMPP_PORT = 2776;
    private static final String SMPP_SYSTEM_ID = "username";
    private static final String SMPP_PASSWORD = "pass1234";
    private static final String SMPP_ADDRESS_RANGE = "6666";
    private static final String DEFAULT_SENDER = "6666";
    private static final String SMS_PAGE = "SendSMS.html";
    private static final int DEFAULT_VALIDITY_PERIOD_MINUTES = 5;

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.sendRedirect(SMS_PAGE);
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        try {

            request.setCharacterEncoding("UTF-8");

            // Form data
            String to = trimToNull(request.getParameter("to"));
            String body = trimToNull(request.getParameter("body"));
            int validityPeriodMinutes = FailedMessageResender.normalizeValidityPeriodMinutes(
                    request.getParameter("validityPeriodMinutes"));

            if (to == null || body == null) {
                response.sendRedirect(SMS_PAGE + "?error=1");
                return;
            }

            // Logged-in user
            HttpSession httpSession = request.getSession(false);

            if (httpSession == null || httpSession.getAttribute("userId") == null) {
                response.sendRedirect("Login.html");
                return;
            }

            int userId;
            try {
                userId = Integer.parseInt(String.valueOf(httpSession.getAttribute("userId")));
            } catch (NumberFormatException e) {
                response.sendRedirect("Login.html");
                return;
            }

            // Database connection
            Connection con = (Connection) getServletContext()
                    .getAttribute("DBConnection");

            if (con == null) {
                throw new IllegalStateException("Database connection is not initialized");
            }

            String msisdn = "";
            String sender = "";

            // Read user information
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT msisdn, twilio_account_sid, "
                    + "twilio_auth_token, twilio_sender_id "
                    + "FROM users WHERE user_id = ?")) {

                ps.setInt(1, userId);

                try (ResultSet rs = ps.executeQuery()) {

                    if (rs.next()) {
                        msisdn = rs.getString("msisdn");
                        sender = rs.getString("twilio_sender_id");
                    }
                }
            }

            if (sender == null || sender.isBlank()) {
                sender = DEFAULT_SENDER;
            }

            // -------------------------
            // Send SMS using SMPP
            // -------------------------
            SMPPSession smppSession = new SMPPSession();

            boolean sendSucceeded = false;

            try {

                smppSession.connectAndBind(
                        SMPP_HOST,
                        SMPP_PORT,
                        new BindParameter(
                                BindType.BIND_TRX,
                                SMPP_SYSTEM_ID,
                                SMPP_PASSWORD,
                                "",
                                TypeOfNumber.UNKNOWN,
                                NumberingPlanIndicator.UNKNOWN,
                                SMPP_ADDRESS_RANGE));

                System.out.println("Connected to SMPP Server");

                String messageId = smppSession.submitShortMessage(
                        "CMT",
                        TypeOfNumber.ALPHANUMERIC,
                        NumberingPlanIndicator.UNKNOWN,
                        sender,                     // Sender ID
                        TypeOfNumber.INTERNATIONAL,
                        NumberingPlanIndicator.ISDN,
                        to,                         // Destination
                        new ESMClass(),
                        (byte) 0,
                        (byte) 1,
                        null,
                        null,
                        new RegisteredDelivery(
                                SMSCDeliveryReceipt.SUCCESS_FAILURE),
                        (byte) 0,
                        new GeneralDataCoding(),
                        (byte) 0,
                        body.getBytes(StandardCharsets.UTF_8));

                System.out.println("SMS sent successfully.");
                System.out.println("Message ID = " + messageId);
                sendSucceeded = true;

                saveMessage(con, msisdn, to, sender, body, false, validityPeriodMinutes);
                response.sendRedirect(SMS_PAGE + "?success=1");

            } catch (Exception e) {
                try {
                    saveMessage(con, msisdn, to, sender, body, true, validityPeriodMinutes);
                    System.out.println("Saved failed SMS to failed_messages table");
                } catch (Exception failedInsertEx) {
                    System.out.println("Failed to save SMS to failed_messages table");
                    failedInsertEx.printStackTrace();
                    e.addSuppressed(failedInsertEx);
                }

                e.printStackTrace();
                response.sendRedirect(SMS_PAGE + "?error=1");
            } finally {

                try {
                    smppSession.unbindAndClose();
                    System.out.println("Disconnected from SMPP Server");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {

            e.printStackTrace();
            response.sendRedirect(SMS_PAGE + "?error=1");
        }
    }

    private void saveMessage(Connection con,
                             String msisdn,
                             String recipient,
                             String sender,
                             String body,
                             boolean failed,
                             int validityPeriodMinutes) throws SQLException {

        String table = failed ? "failed_messages" : "messages";

        // if (failed) {
        //     ensureFailedMessagesSchema(con);
        // }

        System.out.println("Persisting message to table: " + table);

        try {
            insertMessage(con, table, msisdn, recipient, sender, body, failed, validityPeriodMinutes);
        } catch (SQLException e) {
            if (failed && isForeignKeyViolation(e) && msisdn != null && !msisdn.isBlank()) {
                System.out.println("Foreign key violation on msisdn for failed_messages; retrying with NULL");
                insertMessage(con, table, null, recipient, sender, body, failed, validityPeriodMinutes);
            } else {
                throw e;
            }
        }
    }

    private void insertMessage(Connection con,
                               String table,
                               String msisdn,
                               String recipient,
                               String sender,
                               String body,
                               boolean failed,
                               int validityPeriodMinutes) throws SQLException {

        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO ").append(table).append(" ");
        sql.append("(msisdn, recipient_no, sender_no, msg");

        if (failed) {
            sql.append(", validity_period_minutes, next_retry_at");
        }

        sql.append(") VALUES (?, ?, ?, ?");

        if (failed) {
            sql.append(", ?, ?");
        }

        sql.append(")");

        try (PreparedStatement insert = con.prepareStatement(sql.toString())) {

            if (msisdn == null || msisdn.isBlank()) {
                insert.setNull(1, Types.VARCHAR);
            } else {
                insert.setString(1, msisdn);
            }

            insert.setString(2, recipient);
            insert.setString(3, sender);
            insert.setString(4, body);

            if (failed) {
                insert.setInt(5, validityPeriodMinutes);
                insert.setTimestamp(6, Timestamp.from(Instant.now().plus(Duration.ofMinutes(validityPeriodMinutes))));
            }

            int rows = insert.executeUpdate();
            System.out.println("Inserted " + rows + " row(s) into " + table);
        }
    }

    // next-retry timestamps are stored as absolute instants (TIMESTAMPTZ) in the database.
    // We compute instants with UTC semantics and store them directly via JDBC.

    // private void ensureFailedMessagesSchema(Connection con) throws SQLException {
    //     try (PreparedStatement ps = con.prepareStatement(
    //             "ALTER TABLE failed_messages ADD COLUMN IF NOT EXISTS validity_period_minutes INTEGER NOT NULL DEFAULT ?")) {
    //         ps.setInt(1, DEFAULT_VALIDITY_PERIOD_MINUTES);
    //         ps.executeUpdate();
    //     }

    //     try (PreparedStatement ps = con.prepareStatement(
    //             "ALTER TABLE failed_messages ADD COLUMN IF NOT EXISTS next_retry_at TIMESTAMP")) {
    //         ps.executeUpdate();
    //     }
    // }

    private boolean isForeignKeyViolation(SQLException e) {
        String sqlState = e.getSQLState();
        return "23503".equals(sqlState) || (e.getMessage() != null && e.getMessage().toLowerCase().contains("foreign key"));
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}