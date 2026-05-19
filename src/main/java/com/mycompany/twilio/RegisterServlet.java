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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Web application lifecycle listener.
 *
 * @author omar
 */
public class RegisterServlet extends HttpServlet {

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // if logged in
        HttpSession session = request.getSession(false);
        if (session != null && "yes".equals(session.getAttribute("isLoggedIn"))) {
            response.sendRedirect("Profile.html");
            return;
        }
        //create the session
        session = request.getSession(true);

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        String fullName = request.getParameter("fullName");
        String birthDatestr = request.getParameter("birthday");
        //convert to local date 
        LocalDate localDate = LocalDate.parse(birthDatestr, DateTimeFormatter.ISO_DATE);
        // convert to java sql date 
        Date sqlDate = Date.valueOf(localDate);

        String job = request.getParameter("job");
        String password = request.getParameter("password");

        String email = request.getParameter("email");
        String msisdn = request.getParameter("msisdn");        

        String physicalAddress = request.getParameter("physicalAddress");
        // Twilio 
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
            ps.setDate(2, sqlDate);
            ps.setString(3, job);
            ps.setString(4, email);

            ps.setString(5, password);
            Boolean is_admin = false; // by defualt any user is not an admine 

            ps.setBoolean(6, is_admin);
            ps.setString(7, msisdn);
            ps.setString(8, physicalAddress);

            ps.setString(9, twilioSid);
            ps.setString(10, twilioToken);
            ps.setString(11, twilioSender);
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
            
            // Sending the twilio SID and AUTH Token to the OTP generation step via session attributes
            session.setAttribute("msisdn", msisdn);
            session.setAttribute("twilioSid", twilioSid);
            session.setAttribute("twilioToken", twilioToken);
            session.setAttribute("twilioSender", twilioSender);
            request.getRequestDispatcher("/OTPgenerationServlet").forward(request, response);
            // Do NOT close the shared connection - it is managed by DBConnectionInitializer
        } catch (Exception e) {
            out.println("Error: " + e.getMessage());
            e.printStackTrace(out);
        }
    }
}
