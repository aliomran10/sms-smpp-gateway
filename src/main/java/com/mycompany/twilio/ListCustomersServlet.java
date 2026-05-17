package com.mycompany.twilio;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

/**
 * ListCustomersServlet — Admin only.
 *
 * GET /ListCustomersServlet → Queries all non-admin users and writes a
 * self-contained HTML table (styled consistently with the project's existing
 * pages) directly to the response. No JSP or extra HTML file is needed.
 *
 * Optional query-string parameters: search — filters by name, e-mail, or MSISDN
 * (case-insensitive ILIKE)
 *
 * Flash messages read from query string (set by Add/Edit/Delete redirects):
 * success=added | updated | deleted error=notfound | db | badid | selfdelete
 *
 * Admin check: session attribute "is_admin" must equal "true" (set by
 * LoginServlet on successful admin login).
 *
 * Must be registered in web.xml:
 * <servlet-name>ListCustomersServlet</servlet-name>
 * <servlet-class>com.mycompany.twilio.ListCustomersServlet</servlet-class>
 * <url-pattern>/ListCustomersServlet</url-pattern>
 */
public class ListCustomersServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isAdmin(request, response)) {
            return;
        }

        String search = request.getParameter("search");
        if (search == null) {
            search = "";
        }
        search = search.trim();

        String successParam = request.getParameter("success");
        String errorParam = request.getParameter("error");

        ServletContext ctx = getServletContext();
        Connection con = (Connection) ctx.getAttribute("DBConnection");

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        // ── HTML head & styles (matching the project's purple-gradient theme) ─
        out.println("<!DOCTYPE html>");
        out.println("<html lang='en'><head><meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>Customer List - SMS Platform</title>");
        out.println("<style>");
        out.println("* { margin:0; padding:0; box-sizing:border-box; }");
        out.println("body { font-family:'Segoe UI',Tahoma,Geneva,Verdana,sans-serif;"
                + " background:linear-gradient(135deg,#667eea 0%,#764ba2 100%);"
                + " min-height:100vh; padding:30px; }");
        out.println(".card { background:white; border-radius:15px;"
                + " box-shadow:0 20px 60px rgba(0,0,0,0.3); overflow:hidden; }");
        out.println(".header { background:linear-gradient(135deg,#667eea 0%,#764ba2 100%);"
                + " color:white; padding:25px 30px;"
                + " display:flex; justify-content:space-between; align-items:center; }");
        out.println(".header h1 { font-size:22px; }");
        out.println(".header a { color:white; text-decoration:none; font-weight:600;"
                + " background:rgba(255,255,255,0.2); padding:8px 16px;"
                + " border-radius:8px; }");
        out.println(".body { padding:25px 30px; }");
        out.println(".toolbar { display:flex; gap:10px; margin-bottom:20px; }");
        out.println(".toolbar form { display:flex; gap:8px; flex:1; }");
        out.println(".toolbar input { flex:1; padding:10px; border:2px solid #e0e0e0;"
                + " border-radius:8px; font-size:14px; }");
        out.println(".toolbar button, .btn {"
                + " padding:10px 18px; border:none; border-radius:8px;"
                + " cursor:pointer; font-size:14px; font-weight:600; }");
        out.println(".btn-primary { background:linear-gradient(135deg,#667eea,#764ba2);"
                + " color:white; text-decoration:none; }");
        out.println(".btn-search { background:linear-gradient(135deg,#667eea,#764ba2);"
                + " color:white; }");
        out.println(".banner { padding:12px; border-radius:8px; margin-bottom:16px;"
                + " font-size:14px; text-align:center; }");
        out.println(".success { background:#d4edda; color:#155724; border:1px solid #c3e6cb; }");
        out.println(".error   { background:#f8d7da; color:#721c24; border:1px solid #f5c6cb; }");
        out.println("table { width:100%; border-collapse:collapse; font-size:14px; }");
        out.println("th { background:#f4f4f4; padding:12px 10px; text-align:left;"
                + " border-bottom:2px solid #e0e0e0; color:#555; }");
        out.println("td { padding:11px 10px; border-bottom:1px solid #f0f0f0; color:#333; }");
        out.println("tr:hover td { background:#fafafa; }");
        out.println(".badge { padding:3px 9px; border-radius:12px; font-size:12px;"
                + " font-weight:600; }");
        out.println(".verified   { background:#d4edda; color:#155724; }");
        out.println(".unverified { background:#fff3cd; color:#856404; }");
        out.println(".actions a { margin-right:8px; font-size:13px; font-weight:600;"
                + " text-decoration:none; }");
        out.println(".edit-link   { color:#667eea; }");
        out.println(".delete-link { color:#e74c3c; }");
        out.println(".no-data { text-align:center; padding:40px; color:#999; }");
        out.println("</style></head><body>");

        out.println("<div class='card'>");
        out.println("<div class='header'>");
        out.println("<h1>Customer Management</h1>");
        out.println("<a href='AddCustomerServlet'>+ Add Customer</a>");
        out.println("</div>");

        out.println("<div class='body'>");

        // ── Flash banner ──────────────────────────────────────────────────────
        if ("added".equals(successParam)) {
            out.println("<div class='banner success'>Customer added successfully.</div>");
        } else if ("updated".equals(successParam)) {
            out.println("<div class='banner success'>Customer updated successfully.</div>");
        } else if ("deleted".equals(successParam)) {
            out.println("<div class='banner success'>Customer deleted successfully.</div>");
        } else if (errorParam != null) {
            String msg = switch (errorParam) {
                case "notfound" ->
                    "Customer not found.";
                case "selfdelete" ->
                    "You cannot delete your own account.";
                case "db" ->
                    "A database error occurred. Please try again.";
                default ->
                    "An error occurred. Please try again.";
            };
            out.println("<div class='banner error'>" + escapeHtml(msg) + "</div>");
        }

        // ── Search toolbar ────────────────────────────────────────────────────
        out.println("<div class='toolbar'>");
        out.println("<form method='GET' action='ListCustomersServlet'>");
        out.println("<input type='text' name='search' placeholder='Search by name, email or MSISDN…'"
                + " value='" + escapeHtml(search) + "'>");
        out.println("<button type='submit' class='btn-search'>Search</button>");
        if (!search.isEmpty()) {
            out.println("<a href='ListCustomersServlet' class='btn btn-primary'>Clear</a>");
        }
        out.println("</form>");
        out.println("</div>");

        // ── Query DB ──────────────────────────────────────────────────────────
        try {
            PreparedStatement ps;
            boolean hasSearch = !search.isEmpty();

            if (hasSearch) {
                ps = con.prepareStatement(
                        "SELECT user_id, full_name, email, msisdn, job, "
                        + "       is_verified, created_at "
                        + "FROM   users "
                        + "WHERE  is_admin = FALSE "
                        + "  AND (LOWER(full_name) LIKE LOWER(?) "
                        + "       OR LOWER(email)  LIKE LOWER(?) "
                        + "       OR msisdn         LIKE ?) "
                        + "ORDER  BY full_name ASC"
                );
                String pattern = "%" + search + "%";
                ps.setString(1, pattern);
                ps.setString(2, pattern);
                ps.setString(3, pattern);
            } else {
                ps = con.prepareStatement(
                        "SELECT user_id, full_name, email, msisdn, job, "
                        + "       is_verified, created_at "
                        + "FROM   users "
                        + "WHERE  is_admin = FALSE "
                        + "ORDER  BY full_name ASC"
                );
            }

            ResultSet rs = ps.executeQuery();

            // ── Table ─────────────────────────────────────────────────────────
            out.println("<table>");
            out.println("<thead><tr>");
            out.println("<th>#</th>");
            out.println("<th>Full Name</th>");
            out.println("<th>Email</th>");
            out.println("<th>MSISDN</th>");
            out.println("<th>Job</th>");
            out.println("<th>Status</th>");
            out.println("<th>Registered</th>");
            out.println("<th>Actions</th>");
            out.println("</tr></thead><tbody>");

            boolean hasRows = false;
            int rowNum = 1;

            while (rs.next()) {
                hasRows = true;
                int uid = rs.getInt("user_id");
                String fullName = escapeHtml(rs.getString("full_name"));
                String email = escapeHtml(rs.getString("email"));
                String msisdn = escapeHtml(nullToEmpty(rs.getString("msisdn")));
                String job = escapeHtml(nullToEmpty(rs.getString("job")));
                boolean verified = rs.getBoolean("is_verified");
                Timestamp created = rs.getTimestamp("created_at");
                String createdStr = created != null ? created.toString().substring(0, 16) : "—";

                String badge = verified
                        ? "<span class='badge verified'>Verified</span>"
                        : "<span class='badge unverified'>Pending</span>";

                out.println("<tr>");
                out.println("<td>" + rowNum++ + "</td>");
                out.println("<td>" + fullName + "</td>");
                out.println("<td>" + email + "</td>");
                out.println("<td>" + msisdn + "</td>");
                out.println("<td>" + (job.isEmpty() ? "—" : job) + "</td>");
                out.println("<td>" + badge + "</td>");
                out.println("<td>" + createdStr + "</td>");
                out.println("<td class='actions'>"
                        + "<a href='EditCustomerServlet?id=" + uid + "' class='edit-link'>Edit</a>"
                        + "<a href='DeleteCustomerServlet?id=" + uid + "' class='delete-link'>Delete</a>"
                        + "<a href='CustomerStatsServlet?id=" + uid + "' class='edit-link'>Stats</a>"
                        + "</td>");
                out.println("</tr>");
            }

            if (!hasRows) {
                out.println("<tr><td colspan='8' class='no-data'>"
                        + (hasSearch ? "No customers match your search." : "No customers found.")
                        + "</td></tr>");
            }

            out.println("</tbody></table>");

        } catch (Exception e) {
            e.printStackTrace();
            out.println("<div class='banner error'>Failed to load customers: "
                    + escapeHtml(e.getMessage()) + "</div>");
        }

        out.println("</div></div></body></html>");
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

    /**
     * Escapes HTML special characters to prevent XSS in printed output.
     */
    private String escapeHtml(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
