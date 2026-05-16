package com.mycompany.twilio;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * EditCustomerServlet — Admin only.
 *
 * GET /EditCustomerServlet?id={userId} → Loads the customer's current data from
 * the DB and forwards to editCustomer.html (or a JSP) so the form fields can be
 * pre-filled. Because the project uses plain HTML files, customer data is
 * passed as query-string parameters on the redirect so JavaScript can populate
 * the form on the client side.
 *
 * POST /EditCustomerServlet → Reads form parameters (including hidden userId),
 * validates them, updates the users table, then redirects to
 * ListCustomersServlet.
 *
 * Admin check: session attribute "is_admin" must equal "true" (set by
 * LoginServlet on successful admin login).
 *
 * Must be registered in web.xml:
 * <servlet-name>EditCustomerServlet</servlet-name>
 * <servlet-class>com.mycompany.twilio.EditCustomerServlet</servlet-class>
 * <url-pattern>/EditCustomerServlet</url-pattern>
 */
public class EditCustomerServlet extends HttpServlet {

    // ------------------------------------------------------------------ GET --
    // Load the customer row and pass values to the edit form via query string.
    // The edit form reads these with URLSearchParams in JavaScript and fills inputs.
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
                    "SELECT user_id, full_name, birthday, job, email, msisdn, "
                    + "       physical_address, twilio_account_sid, "
                    + "       twilio_auth_token, twilio_sender_id, is_verified "
                    + "FROM   users "
                    + "WHERE  user_id = ? AND is_admin = FALSE"
            );
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                // Customer not found
                response.sendRedirect("ListCustomersServlet?error=notfound");
                return;
            }

            // Build redirect URL with current values as query parameters so the
            // static HTML form can pre-populate all fields via JavaScript.
            String birthday = rs.getDate("birthday") != null
                    ? rs.getDate("birthday").toString() : "";

            String redirectUrl = "editCustomer.html"
                    + "?id=" + rs.getInt("user_id")
                    + "&fullName=" + encode(rs.getString("full_name"))
                    + "&birthday=" + encode(birthday)
                    + "&job=" + encode(nullToEmpty(rs.getString("job")))
                    + "&email=" + encode(rs.getString("email"))
                    + "&msisdn=" + encode(nullToEmpty(rs.getString("msisdn")))
                    + "&address=" + encode(nullToEmpty(rs.getString("physical_address")))
                    + "&twilioSid=" + encode(nullToEmpty(rs.getString("twilio_account_sid")))
                    + "&twilioToken=" + encode(nullToEmpty(rs.getString("twilio_auth_token")))
                    + "&twilioSender=" + encode(nullToEmpty(rs.getString("twilio_sender_id")))
                    + "&isVerified=" + rs.getBoolean("is_verified");

            response.sendRedirect(redirectUrl);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("ListCustomersServlet?error=db");
        }
    }

    // ----------------------------------------------------------------- POST --
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isAdmin(request, response)) {
            return;
        }

        // ── 1. Collect parameters ─────────────────────────────────────────────
        String userIdStr = getParam(request, "userId");
        String fullName = getParam(request, "fullName");
        String birthdayStr = getParam(request, "birthday");
        String job = getParam(request, "job");
        String email = getParam(request, "email");
        String newPassword = getParam(request, "password");   // blank = keep existing
        String msisdn = getParam(request, "msisdn");
        String physicalAddress = getParam(request, "physicalAddress");
        String twilioSid = getParam(request, "twilioSid");
        String twilioToken = getParam(request, "twilioToken");
        String twilioSender = getParam(request, "twilioSender");
        // Checkbox: present = "on", absent = null
        boolean isVerified = "on".equalsIgnoreCase(request.getParameter("isVerified"));

        // ── 2. Validate userId ────────────────────────────────────────────────
        int userId;
        try {
            userId = Integer.parseInt(userIdStr);
        } catch (NumberFormatException e) {
            response.sendRedirect("ListCustomersServlet?error=badid");
            return;
        }

        // ── 3. Required-field check ───────────────────────────────────────────
        if (fullName.isEmpty() || email.isEmpty() || msisdn.isEmpty()
                || twilioSid.isEmpty() || twilioToken.isEmpty() || twilioSender.isEmpty()) {
            response.sendRedirect("editCustomer.html?error=missing&id=" + userId);
            return;
        }

        // ── 4. Parse optional birthday ────────────────────────────────────────
        Date sqlBirthday = null;
        if (!birthdayStr.isEmpty()) {
            try {
                sqlBirthday = Date.valueOf(LocalDate.parse(birthdayStr));
            } catch (DateTimeParseException e) {
                response.sendRedirect("editCustomer.html?error=birthday&id=" + userId);
                return;
            }
        }

        // ── 5. Build UPDATE — only touch password_hash when a new one is given ─
        ServletContext ctx = getServletContext();
        Connection con = (Connection) ctx.getAttribute("DBConnection");

        try {
            PreparedStatement ps;

            if (newPassword.isEmpty()) {
                // Keep existing password_hash unchanged
                ps = con.prepareStatement(
                        "UPDATE users "
                        + "SET full_name          = ?, "
                        + "    birthday           = ?, "
                        + "    job                = ?, "
                        + "    email              = ?, "
                        + "    msisdn             = ?, "
                        + "    physical_address   = ?, "
                        + "    twilio_account_sid = ?, "
                        + "    twilio_auth_token  = ?, "
                        + "    twilio_sender_id   = ?, "
                        + "    is_verified        = ? "
                        + "WHERE user_id = ? AND is_admin = FALSE"
                );
                ps.setString(1, fullName);
                ps.setDate(2, sqlBirthday);
                ps.setString(3, job.isEmpty() ? null : job);
                ps.setString(4, email);
                ps.setString(5, msisdn);
                ps.setString(6, physicalAddress.isEmpty() ? null : physicalAddress);
                ps.setString(7, twilioSid);
                ps.setString(8, twilioToken);
                ps.setString(9, twilioSender);
                ps.setBoolean(10, isVerified);
                ps.setInt(11, userId);

            } else {
                // Also update the password
                ps = con.prepareStatement(
                        "UPDATE users "
                        + "SET full_name          = ?, "
                        + "    birthday           = ?, "
                        + "    job                = ?, "
                        + "    email              = ?, "
                        + "    password_hash      = ?, "
                        + "    msisdn             = ?, "
                        + "    physical_address   = ?, "
                        + "    twilio_account_sid = ?, "
                        + "    twilio_auth_token  = ?, "
                        + "    twilio_sender_id   = ?, "
                        + "    is_verified        = ? "
                        + "WHERE user_id = ? AND is_admin = FALSE"
                );
                ps.setString(1, fullName);
                ps.setDate(2, sqlBirthday);
                ps.setString(3, job.isEmpty() ? null : job);
                ps.setString(4, email);
                ps.setString(5, newPassword);
                ps.setString(6, msisdn);
                ps.setString(7, physicalAddress.isEmpty() ? null : physicalAddress);
                ps.setString(8, twilioSid);
                ps.setString(9, twilioToken);
                ps.setString(10, twilioSender);
                ps.setBoolean(11, isVerified);
                ps.setInt(12, userId);
            }

            int rows = ps.executeUpdate();
            if (rows == 0) {
                response.sendRedirect("ListCustomersServlet?error=notfound");
                return;
            }

            response.sendRedirect("ListCustomersServlet?success=updated");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("editCustomer.html?error=duplicate&id=" + userId);
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

    private String getParam(HttpServletRequest request, String name) {
        String v = request.getParameter(name);
        return (v == null) ? "" : v.trim();
    }

    private String nullToEmpty(String s) {
        return (s == null) ? "" : s;
    }

    /**
     * URL-encodes a string for safe use in a query string.
     */
    private String encode(String s) {
        try {
            return java.net.URLEncoder.encode(s, "UTF-8");
        } catch (Exception e) {
            return s;
        }
    }
}
