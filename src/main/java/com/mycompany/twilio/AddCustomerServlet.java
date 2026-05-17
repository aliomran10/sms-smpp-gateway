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
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * AddCustomerServlet — Admin only.
 *
 * GET /AddCustomerServlet → redirects to the static addCustomer.html form POST
 * /AddCustomerServlet → validates input, inserts new customer into DB, then
 * redirects to ListCustomersServlet
 *
 * Admin check: session attribute "is_admin" must equal "true" (set by
 * LoginServlet on successful admin login).
 *
 * Must be registered in web.xml:
 * <servlet-name>AddCustomerServlet</servlet-name>
 * <servlet-class>com.mycompany.twilio.AddCustomerServlet</servlet-class>
 * <url-pattern>/AddCustomerServlet</url-pattern>
 */
public class AddCustomerServlet extends HttpServlet {

    // ------------------------------------------------------------------ GET --
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isAdmin(request, response)) {
            return;
        }
        response.sendRedirect("addCustomer.html");
    }

    // ----------------------------------------------------------------- POST --
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isAdmin(request, response)) {
            return;
        }

        // ── 1. Collect form parameters ────────────────────────────────────────
        String fullName = getParam(request, "fullName");
        String birthdayStr = getParam(request, "birthday");
        String job = getParam(request, "job");
        String email = getParam(request, "email");
        String password = getParam(request, "password");
        String msisdn = getParam(request, "msisdn");
        String physicalAddress = getParam(request, "physicalAddress");
        String twilioSid = getParam(request, "twilioSid");
        String twilioToken = getParam(request, "twilioToken");
        String twilioSender = getParam(request, "twilioSender");

        // ── 2. Required-field check ───────────────────────────────────────────
        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()
                || msisdn.isEmpty() || twilioSid.isEmpty()
                || twilioToken.isEmpty() || twilioSender.isEmpty()) {
            response.sendRedirect("addCustomer.html?error=missing");
            return;
        }

        // ── 3. Parse optional birthday ────────────────────────────────────────
        Date sqlBirthday = null;
        if (!birthdayStr.isEmpty()) {
            try {
                sqlBirthday = Date.valueOf(LocalDate.parse(birthdayStr));
            } catch (DateTimeParseException e) {
                response.sendRedirect("addCustomer.html?error=birthday");
                return;
            }
        }

        // ── 4. Insert into DB ─────────────────────────────────────────────────
        // Admin-created accounts are marked is_verified = TRUE (no OTP needed).
        // is_admin is FALSE — this endpoint only creates customer accounts.
        ServletContext ctx = getServletContext();
        Connection con = (Connection) ctx.getAttribute("DBConnection");

        try {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO users "
                    + "(full_name, birthday, job, email, password_hash, is_admin, "
                    + " msisdn, physical_address, twilio_account_sid, "
                    + " twilio_auth_token, twilio_sender_id, created_at, is_verified) "
                    + "VALUES (?, ?, ?, ?, ?, FALSE, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, TRUE)"
            );

            ps.setString(1, fullName);
            ps.setDate(2, sqlBirthday);
            ps.setString(3, job.isEmpty() ? null : job);
            ps.setString(4, email);
            ps.setString(5, password);                 // stored as-is, matching project convention
            ps.setString(6, msisdn);
            ps.setString(7, physicalAddress.isEmpty() ? null : physicalAddress);
            ps.setString(8, twilioSid);
            ps.setString(9, twilioToken);
            ps.setString(10, twilioSender);

            ps.executeUpdate();

            response.sendRedirect("ListCustomersServlet?success=added");

        } catch (Exception e) {
            e.printStackTrace();
            // Most likely a duplicate email or MSISDN constraint violation
            response.sendRedirect("addCustomer.html?error=duplicate");
        }
    }

    // ─────────────────────────────────────────── helpers ──────────────────────
    /**
     * Checks that the current session belongs to an admin. Redirects to
     * Login.html and returns false if not.
     */
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

    /**
     * Returns a trimmed parameter value, or "" if null.
     */
    private String getParam(HttpServletRequest request, String name) {
        String v = request.getParameter(name);
        return (v == null) ? "" : v.trim();
    }
}
