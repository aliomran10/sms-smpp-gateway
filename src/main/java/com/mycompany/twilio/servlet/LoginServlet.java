/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mycompany.twilio.servlet;

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
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 *
 * @author omar
 */
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/Login.html");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session != null && "yes".equals(session.getAttribute("isLoggedIn"))) {
            response.sendRedirect("Profile.html");
            return;
        }

        session = request.getSession(true);

        ServletContext ctx = getServletContext();
        String inputEmail = request.getParameter("email");
        String inputPassword = request.getParameter("password");
        Connection con = (Connection) ctx.getAttribute("DBConnection");

        try {
            PreparedStatement pstm = con.prepareStatement(
                    "SELECT msisdn, user_id, email, password_hash, is_admin "
                    + "FROM users WHERE email = ?");
            pstm.setString(1, inputEmail);
            ResultSet rs = pstm.executeQuery();

            if (rs.next()) {
                String databaseEmail = rs.getString("email");
                String databasePassword = rs.getString("password_hash");
                // Use getBoolean — PostgreSQL returns "t"/"f" for booleans,
                // not "true"/"false", so getString + equalsIgnoreCase("true") always fails
                boolean isAdmin = rs.getBoolean("is_admin");
                String msisdn = rs.getString("msisdn");
                int userId = rs.getInt("user_id");

                if (databaseEmail.equals(inputEmail) && databasePassword.equals(inputPassword)) {
                    session.setAttribute("userId", userId);
                    session.setAttribute("is_admin", String.valueOf(isAdmin)); // "true" or "false"
                    session.setAttribute("msisdn", msisdn);
                    session.setAttribute("isLoggedIn", "yes");

                    if (isAdmin) {
                        System.out.println("Admin login: " + inputEmail);
                        response.sendRedirect(request.getContextPath() + "/admin/stats");
                    } else {
                        response.sendRedirect(request.getContextPath() + "/home.jsp");
                    }
                } else {
                    response.sendRedirect("Login.html?error=1");
                }
            } else {
                response.sendRedirect("Login.html?error=1");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
