/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Filter.java to edit this template
 */
package com.mycompany.twilio.filter;

import java.io.IOException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 *
 * @author mohamed
 */
@WebFilter("/*")
public class IsLoginFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;

        HttpServletResponse resp = (HttpServletResponse) response;

        String path = req.getRequestURI();
        // make the login, registration, otp generation and verification pages
        // accessible without login
        boolean allowedPath = path.contains("Login")
                || path.contains("Registration")
                || path.contains("RegisterServlet")
                || path.contains("OTP")
                || path.contains("Verify");

        // skip static pages
        boolean staticResource = path.endsWith(".css")
                || path.endsWith(".js")
                || path.endsWith(".png")
                || path.endsWith(".jpg");

        System.out.println(req.getRequestURI());
        HttpSession session = req.getSession(false);

        // boolean loggedIn = session != null
        // && session.getAttribute("userId") != null;
        // check if user have session,logged in and verified to have id in the database

        boolean loggedIn = session != null
                // && session.getAttribute("userId") != null
                && "yes".equals(session.getAttribute("isLoggedIn"));

        if (loggedIn || allowedPath || staticResource) {

            chain.doFilter(request, response);

        } else {

            resp.sendRedirect("Login.html");
        }

    }

}
