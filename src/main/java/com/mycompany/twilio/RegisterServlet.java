/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mycompany.twilio;

import java.io.*;
import java.sql.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.util.Set;

/**
 * Web application lifecycle listener.
 *
 * @author omar
 */
public class RegisterServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // if logged in
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("isLoggedIn") == "yes") {
            response.sendRedirect("Profile.html");
            return;
        }

        response.sendRedirect("Register.html");
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
// create session 
        HttpSession session = request.getSession(true);

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        String fullName = request.getParameter("fullName");
        String birthDate = request.getParameter("birthday");
        String job = request.getParameter("job");
        String password = request.getParameter("password");

        String email = request.getParameter("email");
        String msisdn = request.getParameter("msisdn");
        session.setAttribute("msisdn", msisdn);

        String physicalAddress = request.getParameter("physicalAddress");
        // Teilio 
        String twilioSid = request.getParameter("twilioSid");
        String twilioToken = request.getParameter("twilioToken");
        String twilioSender = request.getParameter("twilioSender");

        try {
            //getting the connection using servlet context 
            ServletContext ctx = getServletContext();
            Connection con = (Connection) ctx.getAttribute("DBConnection");
            
            PreparedStatement ps = con.prepareStatement("INSERT INTO users "
                    + "(full_name,birthday,job,email,password_hash,is_admin,msisdn,physical_address,"
                    + "twilio_account_sid,twilio_auth_token,twilio_sender_id,created_at,is_verified )"
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, FALSE)");

            ps.setString(1, fullName);
            ps.setString(2, birthDate);
            ps.setString(3, job);
            ps.setString(4, email);

            ps.setString(5, password);
            String is_admin = "0"; // by defualt any user is not an admine 

            ps.setString(6, is_admin);
            ps.setString(5, msisdn);
            ps.setString(5, physicalAddress);

            ps.setString(5, twilioSid);
            ps.setString(5, twilioToken);
            ps.setString(5, twilioSender);
            //ps.setString(5, currentTimeStamp); is set defualt = current time stamp 
            // is verified value is false by defualt 

            int result = ps.executeUpdate();
//            if (result > 0) {
//                out.println("<html><body>");
//                out.println("<h1>Registration Successful!</h1>");
//                out.println("<a href='loginDB.html'>Go to Login</a>");
//                out.println("</body></html>");
//            }

         //   response.sendRedirect("VerifyServlet");
            request.getRequestDispatcher("OTPgenerationServlet").forward(request, response);
            con.close();
        } catch (Exception e) {
            out.println("Error: " + e.getMessage());
            e.printStackTrace(out);
        }
    }
}
