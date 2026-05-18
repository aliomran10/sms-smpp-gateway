/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Filter.java to edit this template
 */
package com.mycompany.twilio;

import java.io.IOException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 *
 * @author mohamed
 */
public class IsLoginFilter implements Filter {
    
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest req =(HttpServletRequest) request;

        HttpServletResponse resp =(HttpServletResponse) response;

        String path =req.getRequestURI();

        boolean allowedPath = path.contains("Login")|| path.contains("Registration");

        HttpSession session =req.getSession(false);

        boolean loggedIn = session != null
                           && session.getAttribute("userId") != null;

        if (loggedIn || allowedPath) {

            chain.doFilter(request, response);

        } else {

            resp.sendRedirect("Login.html");
        }
    
    }
    
    
}
