/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mycompany.twilio.servlet;

import com.mycompany.twilio.dao.MessageDao;
import jakarta.servlet.ServletContext;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.sql.Connection;

/**
 *
 * @author mohamed
 */
@WebServlet("/delete-sms")
public class SmsDeleteServlet extends HttpServlet {

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

        String msgIdStr = request.getParameter("msgId");

        HttpSession session = request.getSession(false);

        ServletContext context = getServletContext();

        try {

            int msgId = Integer.parseInt(msgIdStr);

            Connection con = (Connection) context.getAttribute("DBConnection");

            MessageDao dao = new MessageDao(con);

            boolean deleted = dao.deleteMessage(msgId);

            if (deleted) {

                response.sendRedirect("search-sms");

            } else {

                response.getWriter().println("Delete failed.");
            }

        } catch (Exception e) {

            throw new ServletException(e);
        }
    }
}
