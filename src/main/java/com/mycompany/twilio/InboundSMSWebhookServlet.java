/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package com.mycompany.twilio;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 *
 * @author roqaya
 */


public class InboundSMSWebhookServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        try {

            // Twilio parameters
            String from =
                    request.getParameter("From");

            String to =
                    request.getParameter("To");

            String body =
                    request.getParameter("Body");

            System.out.println("Inbound SMS");

            System.out.println("From: " + from);

            System.out.println("To: " + to);

            System.out.println("Body: " + body);

            // Save to DB
            Connection con =
                    (Connection) getServletContext()
                    .getAttribute("DBConnection");

            PreparedStatement ps =
                    con.prepareStatement(
                    "INSERT INTO messages "
                    + "(recipient_no, sender_no, msg_text) "
                    + "VALUES (?, ?, ?)"
            );

            ps.setString(1, to);

            ps.setString(2, from);

            ps.setString(3, body);

            ps.executeUpdate();

            response.setStatus(
                    HttpServletResponse.SC_OK);

        } catch (Exception e) {

            e.printStackTrace();

            response.setStatus(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}