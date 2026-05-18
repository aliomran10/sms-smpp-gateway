/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mycompany.twilio;

import jakarta.servlet.ServletContext;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
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
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // if logged in
        HttpSession session = request.getSession(false);
        if (session != null && "yes".equals(session.getAttribute("isLoggedIn"))) {
            response.sendRedirect("Profile.html");
            return;
        }
        //create the session
        session = request.getSession(true);

        //getting the context to use database
        ServletContext ctx = getServletContext();
        //getting attribute from login 
        String inputEmail = request.getParameter("email");
        String inputPassword = request.getParameter("password");

        Connection con = (Connection) ctx.getAttribute("DBConnection");
        try {
            PreparedStatement pstm = con.prepareStatement("SELECT msisdn, user_id , email, password_hash, is_admin FROM users WHERE email= ?");
            pstm.setString(1, inputEmail);

            ResultSet rs = pstm.executeQuery();
            //verify and extract from database
            if (rs.next()) {
                // here I can extract any info that other pages would need 
                String databaseEmail = rs.getString("email");
                String databasePassword = rs.getString("password_hash");
                //save the is admin in session so can be used later 
                String is_adimn = rs.getString("is_admin");
                String msisdn = rs.getString("msisdn");
                int userId = rs.getInt("user_id");
                session.setAttribute("is_admin", is_adimn);

                if (databaseEmail.equals(inputEmail) && databasePassword.equals(inputPassword)) {
                    //login success
                    session.setAttribute("userId",userId);

                    session.setAttribute("is_admin",is_adimn);
                    
                    session.setAttribute("msisdn",msisdn);
                    
                    session.setAttribute("isLoggedIn","yes");
                    
                    response.sendRedirect("home");
                } else {
                    //wrong email or password 
                    response.sendRedirect("Login.html?error=1");
                }

            } else {
                //so the inserted email doesnt exist or wrong email input from user 
                response.sendRedirect("Login.html?error=1");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
