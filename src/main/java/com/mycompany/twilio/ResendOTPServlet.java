/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mycompany.twilio;

import com.twilio.rest.api.v2010.account.Message;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Random;

/**
 *
 * @author roqaya
 */


public class ResendOTPServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        try {

            HttpSession session =
                    request.getSession(false);

            if (session == null) {

                response.sendRedirect("Login.html");

                return;
            }

            int userId =
                    (int) session.getAttribute("userId");

            String msisdn =
                    (String) session.getAttribute("msisdn");

            Connection con =
                    (Connection) getServletContext()
                    .getAttribute("DBConnection");

            // Get Twilio credentials
            PreparedStatement ps =
                    con.prepareStatement(
                    "SELECT twilio_account_sid, "
                    + "twilio_auth_token, "
                    + "twilio_sender_id "
                    + "FROM users "
                    + "WHERE user_id = ?"
            );

            ps.setInt(1, userId);

            ResultSet rs = ps.executeQuery();

            String sid = "";
            String token = "";
            String sender = "";

            if (rs.next()) {

                sid =
                        rs.getString("twilio_account_sid");

                token =
                        rs.getString("twilio_auth_token");

                sender =
                        rs.getString("twilio_sender_id");
            }

            // Generate OTP
            Random random = new Random();

            int otp =
                    100000 + random.nextInt(900000);

            // Save OTP in session
            session.setAttribute(
                    "OTP",
                    String.valueOf(otp));

            // Send SMS
            Message message =
                    TwilioService.sendSMS(
                    sid,
                    token,
                    sender,
                    msisdn,
                    "Your verification code is: "
                    + otp
            );

            response.sendRedirect(
                    "Verify.html?resent=1");

        } catch (Exception e) {

            e.printStackTrace();

            response.getWriter().write(
                    "Failed to resend OTP");
        }
    }
}