/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mycompany.twilio;

import java.io.*;
import java.sql.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.util.Random;
// Twilio SDK Imports
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
/**
 *
 * @author omar
 */
public class VerifyServlet extends HttpServlet {
    // the twilio auth for the developer that will send to the user 
    
    public static final String ACCOUNT_SID = "ACxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
    public static final String AUTH_TOKEN = "your_auth_token_here";
    public static final String DEV_MSISDN="01200018782";
    
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("isLoggedIn") == "yes") {
            response.sendRedirect("Profile.html");
            return;
        }

        String msisdn = (String) session.getAttribute("msisdn");
        // generate the random OTP verification code 
        Random rand = new Random();
        StringBuilder sb = new StringBuilder();
        // dynamically set the length 
        int length=5;
        for (int i = 0; i < length; i++) {
            sb.append(rand.nextInt(10)); // 0-9
        }
        String otp = sb.toString();
        
        //send the verification code 
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        
         Message message = Message.creator(
                    new PhoneNumber(msisdn),   // To
                    new PhoneNumber(DEV_MSISDN), // From
                    otp                   // Body
            ).create();
         
         // get the verification code entered by the client 
         String EnteredOTP=request.getParameter("otp");
         if(otp != EnteredOTP){
             
         }else { 
             
         }

        PreparedStatement ps = con.prepareStatement("SELECT * FROM users WHERE msisdn = ? AND verification_code = ?");
        ps.setString(1, msisdn);
        ps.setString(2, codeEntered);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            // Code matches → mark as verified
            PreparedStatement psUpdate = con.prepareStatement(
                    "UPDATE users SET is_verified = TRUE, verification_code = NULL WHERE msisdn = ?"
            );
            psUpdate.setString(1, msisdn);
            psUpdate.executeUpdate();

            response.sendRedirect("Profile.html"); // or ProfileServlet
        } else {
            // Verification failed → delete user
            PreparedStatement psDelete = con.prepareStatement(
                    "DELETE FROM users WHERE msisdn = ? AND is_verified = FALSE"
            );
            psDelete.setString(1, msisdn);
            psDelete.executeUpdate();

            response.sendRedirect("Register.html");
        }

    }
