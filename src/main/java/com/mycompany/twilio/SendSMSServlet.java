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

/**
 *
 * @author roqaya
 */

public class SendSMSServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        response.sendRedirect("SendSMS.html");
    }
    
    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        try {

            // Get form data
            String to =
                    request.getParameter("to");

            String body =
                    request.getParameter("body");

            // Get session
            HttpSession session =
                    request.getSession(false);

            if (session == null) {

                response.sendRedirect("Login.html");

                return;
            }

            // Logged in user id
            int userId =
                    (int) session.getAttribute("userId");

            // DB connection
            Connection con =
                    (Connection) getServletContext()
                    .getAttribute("DBConnection");

            // Get Twilio credentials
            PreparedStatement ps =
                    con.prepareStatement(
                    "SELECT msisdn, "
                    + "twilio_account_sid, "
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
            String msisdn = "";

            if (rs.next()) {

                msisdn =
                        rs.getString("msisdn");

                sid =
                        rs.getString("twilio_account_sid");

                token =
                        rs.getString("twilio_auth_token");

                sender =
                        rs.getString("twilio_sender_id");
            }

            // Send SMS
            Message message =
                    TwilioService.sendSMS(
                    sid,
                    token,
                    sender,
                    to,
                    body
            );

            // Save message to DB
            PreparedStatement insert =
                    con.prepareStatement(
                    "INSERT INTO messages "
                    + "(msisdn, recipient_no, "
                    + "sender_no, msg_text, user) "
                    + "VALUES (?, ?, ?, ?, ?)"
            );

            insert.setString(1, msisdn);

            insert.setString(2, to);

            insert.setString(3, sender);

            insert.setString(4, body);

            insert.setInt(5, userId);

            insert.executeUpdate();

            response.sendRedirect(
                    "sendSMS.html?success=1");

        } catch (Exception e) {

            e.printStackTrace();

            response.sendRedirect(
                    "sendSMS.html?error=1");
        }
    }
}