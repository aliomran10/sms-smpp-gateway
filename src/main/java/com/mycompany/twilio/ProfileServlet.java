/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mycompany.twilio;

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
@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {

     @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        HttpSession session =
                request.getSession(false);

        
        int userId =
                (Integer) session.getAttribute("userId");

        ServletContext context =
                getServletContext();

        Connection con =
                (Connection)
                        context.getAttribute(
                                "DBConnection"
                        );

        UserDao userDao = new UserDao(con);

        User user =
                userDao.getUserById(userId);

        request.setAttribute("user", user);

        request.getRequestDispatcher(
                "/profile.jsp"
        ).forward(request, response);
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        HttpSession session =
                request.getSession(false);


        int userId =
                (Integer) session.getAttribute("userId");

        ServletContext context =
                getServletContext();

        Connection con =
                (Connection)
                        context.getAttribute(
                                "DBConnection"
                        );

        User user = new User();

        user.setUserId(userId);

        user.setFullName(
                request.getParameter("fullName")
        );

        user.setEmail(
                request.getParameter("email")
        );

        user.setMsisdn(
                request.getParameter("msisdn")
        );

        user.setJob(
                request.getParameter("job")
        );

        user.setBirthday(
                request.getParameter("birthday")
        );

        user.setPhysicalAddress(
                request.getParameter(
                        "physicalAddress"
                )
        );

        user.setTwilioAccountSid(
                request.getParameter(
                        "accountSid"
                )
        );

        user.setTwilioAuthToken(
                request.getParameter(
                        "authToken"
                )
        );

        user.setTwilioSenderId(
                request.getParameter(
                        "senderId"
                )
        );

        UserDao userDao =
                new UserDao(con);

        boolean updated =
                userDao.updateUser(user);

        if (updated) {

            response.sendRedirect("profile");

        } else {

            response.getWriter()
                    .println(
                            "Profile Update Failed"
                    );
        }
    }

}
