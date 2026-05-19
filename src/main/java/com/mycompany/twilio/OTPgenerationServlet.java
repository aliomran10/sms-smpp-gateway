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
  ///Hard coded for Testing not used in prod  
//    public static final String ACCOUNT_SID = "ACxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
//    public static final String AUTH_TOKEN = "XXXXXXXXXX";
//    public static final String DEV_MSISDN="012XXXXXXXXXX";
//    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
           HttpSession session = request.getSession(false);
           if(session == null){
               response.sendRedirect("Login.html");
               return ;
           }
         
          /// Getting session attributes 
        String msisdn = (String) session.getAttribute("msisdn"); /// the number registerd wit in twilio (012000 ....)
        String ACCOUNT_SID= (String) session.getAttribute("twilioSid"); 
        String AUTH_TOKEN =(String) session.getAttribute("twilioToken");
        String DEV_MSISDN =(String) session.getAttribute("twilioSender"); // twilio trail account number (+1......) 
        
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
                    new PhoneNumber(msisdn),   //send to
                    new PhoneNumber(DEV_MSISDN), //from
                    otp                  
            ).create();
         
         // save the OTP in session attribute 
         session.setAttribute("OTP", otp);
         
         // redirect to verification 
         response.sendRedirect("VerifyServlet");
         
         
        }
    }

