package com.mycompany.twilio;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * DeleteCustomerServlet — Admin only.
 *
 * GET /DeleteCustomerServlet?id={userId} → Loads the customer's name and
 * e-mail, then redirects to deleteCustomer.html with those details as query
 * parameters so the static HTML can show a confirmation message.
 *
 * POST /DeleteCustomerServlet → Receives the hidden userId from the
 * confirmation form, deletes the customer from the DB (messages are removed
 * automatically via ON DELETE CASCADE), then redirects to ListCustomersServlet.
 *
 * Admin check: session attribute "is_admin" must equal "true" (set by
 * LoginServlet on successful admin login).
 *
 * Must be registered in web.xml:
 * <servlet-name>DeleteCustomerServlet</servlet-name>
 * <servlet-class>com.mycompany.twilio.DeleteCustomerServlet</servlet-class>
 * <url-pattern>/DeleteCustomerServlet</url-pattern>
 */
public class DeleteCustomerServlet extends HttpServlet {

    // ------------------------------------------------------------------ GET --
    // Show a confirmation page with the customer's basic info before deleting.
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isAdmin(request, response)) {
            return;
        }

        String idParam = request.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect("ListCustomersServlet");
            return;
        }

        int userId;
        try {
            userId = Integer.parseInt(idParam.trim());
        } catch (NumberFormatException e) {
            response.sendRedirect("ListCustomersServlet");
            return;
        }

        ServletContext ctx = getServletContext();
        Connection con = (Connection) ctx.getAttribute("DBConnection");

        try {
            PreparedStatement ps = con.prepareStatement(
                    "SELECT user_id, full_name, email, msisdn "
                    + "FROM   users "
                    + "WHERE  user_id = ? AND is_admin = FALSE"
            );
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                response.sendRedirect("ListCustomersServlet?error=notfound");
                return;
            }

            // Pass just enough info for the confirmation page to display
            String redirectUrl = "deleteCustomer.html"
                    + "?id=" + rs.getInt("user_id")
                    + "&fullName=" + encode(rs.getString("full_name"))
                    + "&email=" + encode(rs.getString("email"))
                    + "&msisdn=" + encode(nullToEmpty(rs.getString("msisdn")));

            response.sendRedirect(redirectUrl);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("ListCustomersServlet?error=db");
        }
    }

    // ----------------------------------------------------------------- POST --
    // The confirmation form submits here with the hidden userId field.
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isAdmin(request, response)) {
            return;
        }

        String userIdStr = request.getParameter("userId");
        int userId;
        try {
            userId = Integer.parseInt(userIdStr.trim());
        } catch (Exception e) {
            response.sendRedirect("ListCustomersServlet?error=badid");
            return;
        }

        // Safety: never allow deleting an admin via this endpoint
        HttpSession session = request.getSession(false);
        Object sessionUserId = session.getAttribute("userId");
        if (sessionUserId != null && (int) sessionUserId == userId) {
            response.sendRedirect("ListCustomersServlet?error=selfdelete");
            return;
        }

        ServletContext ctx = getServletContext();
        Connection con = (Connection) ctx.getAttribute("DBConnection");

        try {
            // WHERE is_admin = FALSE prevents accidentally deleting an admin account.
            // Messages are deleted automatically by ON DELETE CASCADE on messages.msisdn.
            PreparedStatement ps = con.prepareStatement(
                    "DELETE FROM users WHERE user_id = ? AND is_admin = FALSE"
            );
            ps.setInt(1, userId);
            int rows = ps.executeUpdate();

            if (rows == 0) {
                response.sendRedirect("ListCustomersServlet?error=notfound");
                return;
            }

            response.sendRedirect("ListCustomersServlet?success=deleted");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("ListCustomersServlet?error=db");
        }
    }

    // ─────────────────────────────────────────── helpers ──────────────────────
    private boolean isAdmin(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null
                || !"true".equals(String.valueOf(session.getAttribute("is_admin")))) {
            response.sendRedirect("Login.html");
            return false;
        }
        return true;
    }

    private String nullToEmpty(String s) {
        return (s == null) ? "" : s;
    }

    private String encode(String s) {
        try {
            return java.net.URLEncoder.encode(s, "UTF-8");
        } catch (Exception e) {
            return s;
        }
    }
}
