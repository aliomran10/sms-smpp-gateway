/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mycompany.twilio;


import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;
/**
 *
 * @author mohamed
 */


@WebServlet("/search-sms")
public class SmsSearchServlet extends HttpServlet {

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {
        
        HttpSession session =
                request.getSession(false);
  
        String msisdn = (String) session.getAttribute("msisdn");
        String keyword = request.getParameter("keyword");
        String fromDate = request.getParameter("fromDate");
        String toDate = request.getParameter("toDate");
        ServletContext context = getServletContext();

        

        try {  
            
           Connection con = (Connection)context.getAttribute("DBConnection");
           MessageDao dao =
                    new MessageDao(con);

            List<Message> messages =
                    dao.searchMessages(
                            msisdn,
                            keyword,
                            fromDate,
                            toDate
                    );

            request.setAttribute(
                    "messages",
                    messages
            );

            request.getRequestDispatcher(
                    "smsMangement.jsp"
            ).forward(request, response);

        } catch (Exception e) {

            throw new ServletException(e);
        }
    }
}
