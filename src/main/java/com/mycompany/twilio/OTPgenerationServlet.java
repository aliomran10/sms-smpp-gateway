/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mycompany.twilio;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
// Twilio SDK Imports
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.servlet.http.HttpSession;
/**
 *
 * @author omar
 */
public class OTPgenerationServlet extends HttpServlet {
  // the twilio auth for the developer that will send to the user 
    
    public static final String ACCOUNT_SID = "ACxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
    public static final String AUTH_TOKEN = "your_auth_token_here";
    public static final String DEV_MSISDN="01200018782";
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
           HttpSession session = request.getSession(false);
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
                    new PhoneNumber(msisdn),   
                    new PhoneNumber(DEV_MSISDN), 
                    otp                  
            ).create();
         
         // save the OTP in session attribute 
         session.setAttribute("OTP", otp);
         
         // redirect to verification 
         response.sendRedirect("VerifyServlet");
         
         
        }
    }

