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
 * CustomerStatsServlet — Admin only.
 *
 * Two modes selected by the presence of the "id" query parameter:
 *
 * GET /CustomerStatsServlet → Summary table: all customers with their total SMS
 * count, ordered by most messages sent.
 *
 * GET /CustomerStatsServlet?id={uid} → Detail view: one customer's profile info
 * + a per-day message breakdown.
 *
 * Both modes write self-contained HTML directly to the response (same approach
 * as ListCustomersServlet — no JSP required).
 *
 * Admin check: session attribute "is_admin" must equal "true" (set by
 * LoginServlet on successful admin login).
 *
 * Must be registered in web.xml:
 * <servlet-name>CustomerStatsServlet</servlet-name>
 * <servlet-class>com.mycompany.twilio.CustomerStatsServlet</servlet-class>
 * <url-pattern>/CustomerStatsServlet</url-pattern>
 */
public class CustomerStatsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isAdmin(request, response)) {
            return;
        }

        String idParam = request.getParameter("id");

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        if (idParam != null && !idParam.trim().isEmpty()) {
            // ── Detail view for one customer ──────────────────────────────────
            int userId;
            try {
                userId = Integer.parseInt(idParam.trim());
            } catch (NumberFormatException e) {
                response.sendRedirect("ListCustomersServlet");
                return;
            }
            renderDetail(out, userId, request);
        } else {
            // ── Summary view for all customers ────────────────────────────────
            renderSummary(out, request);
        }
    }

    // ──────────────────────────────────────── summary view ────────────────────
    private void renderSummary(PrintWriter out, HttpServletRequest request) {

        String search = request.getParameter("search");
        if (search == null) {
            search = "";
        }
        search = search.trim();

        ServletContext ctx = request.getServletContext();
        Connection con = (Connection) ctx.getAttribute("DBConnection");

        printHtmlHead(out, "Customer SMS Statistics");

        out.println("<div class='card'>");
        out.println("<div class='header'>");
        out.println("<h1>SMS Statistics — All Customers</h1>");
        out.println("<a href='ListCustomersServlet'>← Back to Customers</a>");
        out.println("</div><div class='body'>");

        // Search toolbar
        out.println("<div class='toolbar'>");
        out.println("<form method='GET' action='CustomerStatsServlet'>");
        out.println("<input type='text' name='search' "
                + "placeholder='Filter by name, email or MSISDN…' "
                + "value='" + escapeHtml(search) + "'>");
        out.println("<button type='submit' class='btn-search'>Filter</button>");
        if (!search.isEmpty()) {
            out.println("<a href='CustomerStatsServlet' class='btn btn-primary'>Clear</a>");
        }
        out.println("</form>");
        out.println("</div>");

        try {
            // Grand total
            PreparedStatement totalPs = con.prepareStatement(
                    "SELECT COUNT(*) AS grand_total FROM messages"
            );
            ResultSet totalRs = totalPs.executeQuery();
            long grandTotal = 0;
            if (totalRs.next()) {
                grandTotal = totalRs.getLong("grand_total");
            }

            out.println("<p style='margin-bottom:16px;color:#555;font-size:14px;'>"
                    + "Total SMS sent across all accounts: "
                    + "<strong>" + grandTotal + "</strong></p>");

            // Per-customer stats (LEFT JOIN so customers with 0 messages show up)
            boolean hasSearch = !search.isEmpty();
            PreparedStatement ps;
            if (hasSearch) {
                ps = con.prepareStatement(
                        "SELECT u.user_id, u.full_name, u.email, u.msisdn, "
                        + "       COUNT(m.msg_id) AS total_sms, "
                        + "       MAX(m.sent_at)  AS last_sent "
                        + "FROM   users u "
                        + "LEFT JOIN messages m ON m.msisdn = u.msisdn "
                        + "WHERE  u.is_admin = FALSE "
                        + "  AND (LOWER(u.full_name) LIKE LOWER(?) "
                        + "       OR LOWER(u.email)  LIKE LOWER(?) "
                        + "       OR u.msisdn         LIKE ?) "
                        + "GROUP  BY u.user_id, u.full_name, u.email, u.msisdn "
                        + "ORDER  BY total_sms DESC, u.full_name ASC"
                );
                String p = "%" + search + "%";
                ps.setString(1, p);
                ps.setString(2, p);
                ps.setString(3, p);
            } else {
                ps = con.prepareStatement(
                        "SELECT u.user_id, u.full_name, u.email, u.msisdn, "
                        + "       COUNT(m.msg_id) AS total_sms, "
                        + "       MAX(m.sent_at)  AS last_sent "
                        + "FROM   users u "
                        + "LEFT JOIN messages m ON m.msisdn = u.msisdn "
                        + "WHERE  u.is_admin = FALSE "
                        + "GROUP  BY u.user_id, u.full_name, u.email, u.msisdn "
                        + "ORDER  BY total_sms DESC, u.full_name ASC"
                );
            }

            ResultSet rs = ps.executeQuery();

            out.println("<table>");
            out.println("<thead><tr>"
                    + "<th>#</th>"
                    + "<th>Full Name</th>"
                    + "<th>Email</th>"
                    + "<th>MSISDN</th>"
                    + "<th>Total SMS Sent</th>"
                    + "<th>Last Sent</th>"
                    + "<th>Details</th>"
                    + "</tr></thead><tbody>");

            boolean hasRows = false;
            int rowNum = 1;
            while (rs.next()) {
                hasRows = true;
                int uid = rs.getInt("user_id");
                String name = escapeHtml(rs.getString("full_name"));
                String email = escapeHtml(rs.getString("email"));
                String msisdn = escapeHtml(nullToEmpty(rs.getString("msisdn")));
                long total = rs.getLong("total_sms");
                Timestamp lastSent = rs.getTimestamp("last_sent");
                String lastStr = lastSent != null
                        ? lastSent.toString().substring(0, 16) : "—";

                out.println("<tr>");
                out.println("<td>" + rowNum++ + "</td>");
                out.println("<td>" + name + "</td>");
                out.println("<td>" + email + "</td>");
                out.println("<td>" + msisdn + "</td>");
                out.println("<td><strong>" + total + "</strong></td>");
                out.println("<td>" + lastStr + "</td>");
                out.println("<td><a href='CustomerStatsServlet?id=" + uid
                        + "' class='edit-link'>View Detail</a></td>");
                out.println("</tr>");
            }

            if (!hasRows) {
                out.println("<tr><td colspan='7' class='no-data'>"
                        + (hasSearch ? "No customers match your filter."
                                : "No customers found.")
                        + "</td></tr>");
            }

            out.println("</tbody></table>");

        } catch (Exception e) {
            e.printStackTrace();
            out.println("<div class='banner error'>Failed to load statistics: "
                    + escapeHtml(e.getMessage()) + "</div>");
        }

        out.println("</div></div></body></html>");
    }

    // ──────────────────────────────────────────── detail view ─────────────────
    private void renderDetail(PrintWriter out, int userId, HttpServletRequest request) {

        ServletContext ctx = request.getServletContext();
        Connection con = (Connection) ctx.getAttribute("DBConnection");

        printHtmlHead(out, "Customer Stats Detail");

        out.println("<div class='card'>");
        out.println("<div class='header'>");
        out.println("<h1>Customer SMS Detail</h1>");
        out.println("<a href='CustomerStatsServlet'>← Back to Stats</a>");
        out.println("</div><div class='body'>");

        try {
            // ── Customer summary line ─────────────────────────────────────────
            PreparedStatement ps = con.prepareStatement(
                    "SELECT u.user_id, u.full_name, u.email, u.msisdn, "
                    + "       COUNT(m.msg_id) AS total_sms, "
                    + "       MAX(m.sent_at)  AS last_sent "
                    + "FROM   users u "
                    + "LEFT JOIN messages m ON m.msisdn = u.msisdn "
                    + "WHERE  u.user_id = ? AND u.is_admin = FALSE "
                    + "GROUP  BY u.user_id, u.full_name, u.email, u.msisdn"
            );
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                out.println("<div class='banner error'>Customer not found.</div>");
                out.println("</div></div></body></html>");
                return;
            }

            String name = escapeHtml(rs.getString("full_name"));
            String email = escapeHtml(rs.getString("email"));
            String msisdn = escapeHtml(nullToEmpty(rs.getString("msisdn")));
            long total = rs.getLong("total_sms");
            Timestamp last = rs.getTimestamp("last_sent");
            String lastStr = last != null ? last.toString().substring(0, 16) : "—";

            // Customer info card
            out.println("<div style='background:#f7f7f7;border-radius:10px;"
                    + "padding:16px 20px;margin-bottom:20px;"
                    + "border:2px solid #e0e0e0;'>");
            out.println("<p style='font-size:18px;font-weight:700;margin-bottom:8px;'>"
                    + name + "</p>");
            out.println("<p style='color:#555;font-size:14px;'>"
                    + "Email: <strong>" + email + "</strong> &nbsp;|&nbsp; "
                    + "MSISDN: <strong>" + (msisdn.isEmpty() ? "—" : msisdn) + "</strong></p>");
            out.println("<p style='color:#555;font-size:14px;margin-top:6px;'>"
                    + "Total SMS sent: <strong style='font-size:20px;color:#667eea;'>"
                    + total + "</strong> &nbsp;|&nbsp; "
                    + "Last sent: <strong>" + lastStr + "</strong></p>");
            out.println("</div>");

            // ── Per-day breakdown ─────────────────────────────────────────────
            PreparedStatement dayPs = con.prepareStatement(
                    "SELECT DATE(m.sent_at) AS day, COUNT(*) AS sms_sent "
                    + "FROM   messages m "
                    + "JOIN   users    u ON u.msisdn = m.msisdn "
                    + "WHERE  u.user_id = ? "
                    + "GROUP  BY DATE(m.sent_at) "
                    + "ORDER  BY day DESC"
            );
            dayPs.setInt(1, userId);
            ResultSet dayRs = dayPs.executeQuery();

            out.println("<h3 style='margin-bottom:12px;color:#555;'>Messages by Day</h3>");
            out.println("<table>");
            out.println("<thead><tr>"
                    + "<th>Date</th>"
                    + "<th>SMS Sent</th>"
                    + "</tr></thead><tbody>");

            boolean hasDays = false;
            while (dayRs.next()) {
                hasDays = true;
                String day = escapeHtml(dayRs.getDate("day").toString());
                long sent = dayRs.getLong("sms_sent");
                out.println("<tr>");
                out.println("<td>" + day + "</td>");
                out.println("<td><strong>" + sent + "</strong></td>");
                out.println("</tr>");
            }

            if (!hasDays) {
                out.println("<tr><td colspan='2' class='no-data'>"
                        + "This customer has not sent any messages yet."
                        + "</td></tr>");
            }

            out.println("</tbody></table>");

        } catch (Exception e) {
            e.printStackTrace();
            out.println("<div class='banner error'>Failed to load detail: "
                    + escapeHtml(e.getMessage()) + "</div>");
        }

        out.println("</div></div></body></html>");
    }

    // ─────────────────────────────────────────── helpers ──────────────────────
    /**
     * Prints the shared HTML head and opening body, matching the project theme.
     */
    private void printHtmlHead(PrintWriter out, String title) {
        out.println("<!DOCTYPE html>");
        out.println("<html lang='en'><head><meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>" + escapeHtml(title) + " - SMS Platform</title>");
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
                + " background:rgba(255,255,255,0.2); padding:8px 16px; border-radius:8px; }");
        out.println(".body { padding:25px 30px; }");
        out.println(".toolbar { display:flex; gap:10px; margin-bottom:20px; }");
        out.println(".toolbar form { display:flex; gap:8px; flex:1; }");
        out.println(".toolbar input { flex:1; padding:10px; border:2px solid #e0e0e0;"
                + " border-radius:8px; font-size:14px; }");
        out.println(".toolbar button, .btn { padding:10px 18px; border:none;"
                + " border-radius:8px; cursor:pointer; font-size:14px; font-weight:600; }");
        out.println(".btn-primary { background:linear-gradient(135deg,#667eea,#764ba2);"
                + " color:white; text-decoration:none; }");
        out.println(".btn-search  { background:linear-gradient(135deg,#667eea,#764ba2);"
                + " color:white; }");
        out.println(".banner { padding:12px; border-radius:8px; margin-bottom:16px;"
                + " font-size:14px; text-align:center; }");
        out.println(".error { background:#f8d7da; color:#721c24; border:1px solid #f5c6cb; }");
        out.println("table { width:100%; border-collapse:collapse; font-size:14px; }");
        out.println("th { background:#f4f4f4; padding:12px 10px; text-align:left;"
                + " border-bottom:2px solid #e0e0e0; color:#555; }");
        out.println("td { padding:11px 10px; border-bottom:1px solid #f0f0f0; color:#333; }");
        out.println("tr:hover td { background:#fafafa; }");
        out.println(".no-data { text-align:center; padding:40px; color:#999; }");
        out.println(".edit-link { color:#667eea; font-weight:600; text-decoration:none; }");
        out.println("</style></head><body>");
    }

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
