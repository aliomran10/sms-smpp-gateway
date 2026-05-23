package com.mycompany.twilio.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import java.io.IOException;

/**
 *
 * @author Ali
 */
/**
 * Security filter that restricts the /admin/* URL space to logged-in
 * administrators only.
 *
 * Works alongside the existing IsLoginFilter (which handles general
 * authentication). This filter adds the extra privilege check.
 */
@WebFilter(urlPatterns = {"/admin", "/admin/*"})
public class AdminFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);

        boolean loggedIn = session != null
                && "yes".equals(session.getAttribute("isLoggedIn"));

        boolean isAdmin = loggedIn
                && "true".equalsIgnoreCase(
                        String.valueOf(session.getAttribute("is_admin")));

        if (isAdmin) {
            // Redirect bare /admin to /admin/stats
            String uri = req.getRequestURI();
            String contextPath = req.getContextPath();
            if (uri.equals(contextPath + "/admin") || uri.equals(contextPath + "/admin/")) {
                resp.sendRedirect(contextPath + "/admin/stats");
                return;
            }
            chain.doFilter(request, response);
        } else if (loggedIn) {
            // Authenticated but not an admin → customer home
            resp.sendRedirect(req.getContextPath() + "/home");
        } else {
            // Not logged in → login page
            resp.sendRedirect(req.getContextPath() + "/Login.html");
        }
    }
}
