/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.twilio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 *
 * @author mohamed
 */
public class UserDao {

    private final Connection con;

    public UserDao(Connection con) {

        this.con = con;
    }

    public User getUserById(int userId) {

        String sql = """
                SELECT *
                FROM users
                WHERE user_id = ?
                """;

        try (
                PreparedStatement ps =
                        con.prepareStatement(sql)
        ) {

            ps.setInt(1, userId);

            ResultSet rs =
                    ps.executeQuery();

            if (rs.next()) {

                User user = new User();

                user.setUserId(
                        rs.getInt("user_id")
                );

                user.setFullName(
                        rs.getString("full_name")
                );

                user.setBirthday(
                        rs.getString("birthday")
                );

                user.setJob(
                        rs.getString("job")
                );

                user.setEmail(
                        rs.getString("email")
                );

                user.setMsisdn(
                        rs.getString("msisdn")
                );

                user.setPhysicalAddress(
                        rs.getString(
                                "physical_address"
                        )
                );

                user.setTwilioAccountSid(
                        rs.getString(
                                "twilio_account_sid"
                        )
                );

                user.setTwilioAuthToken(
                        rs.getString(
                                "twilio_auth_token"
                        )
                );

                user.setTwilioSenderId(
                        rs.getString(
                                "twilio_sender_id"
                        )
                );

                return user;
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return null;
    }

    public boolean updateUser(User user) {

        String sql = """
                UPDATE users
                SET
                    full_name = ?,
                    birthday = ?,
                    job = ?,
                    email = ?,
                    msisdn = ?,
                    physical_address = ?,
                    twilio_account_sid = ?,
                    twilio_sender_id = ?
                WHERE user_id = ?
                """;

        try (
                PreparedStatement ps =
                        con.prepareStatement(sql)
        ) {

            ps.setString(
                    1,
                    user.getFullName()
            );

            ps.setDate(
                    2,
                    java.sql.Date.valueOf(
                            user.getBirthday()
                    )
            );

            ps.setString(
                    3,
                    user.getJob()
            );

            ps.setString(
                    4,
                    user.getEmail()
            );

            ps.setString(
                    5,
                    user.getMsisdn()
            );

            ps.setString(
                    6,
                    user.getPhysicalAddress()
            );

            ps.setString(
                    7,
                    user.getTwilioAccountSid()
            );

            ps.setString(
                    8,
                    user.getTwilioSenderId()
            );

            ps.setInt(
                    9,
                    user.getUserId()
            );

            int rows =
                    ps.executeUpdate();

            if (user.getTwilioAuthToken() != null
                    && !user.getTwilioAuthToken().isBlank()) {

                updateAuthToken(
                        user.getUserId(),
                        user.getTwilioAuthToken()
                );
            }

            return rows > 0;

        } catch (Exception e) {

            e.printStackTrace();
        }

        return false;
    }

    private void updateAuthToken(
            int userId,
            String token
    ) {

        String sql = """
                UPDATE users
                SET twilio_auth_token = ?
                WHERE user_id = ?
                """;

        try (
                PreparedStatement ps =
                        con.prepareStatement(sql)
        ) {

            ps.setString(1, token);

            ps.setInt(2, userId);

            ps.executeUpdate();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}
