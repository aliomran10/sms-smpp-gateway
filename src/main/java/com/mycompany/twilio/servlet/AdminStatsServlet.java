package com.mycompany.twilio.servlet;

import com.mycompany.twilio.model.CustomerStats;
import com.mycompany.twilio.dao.AdminDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;

/**
 * GET /admin/stats
 *   Renders the statistics overview page (adminStats.jsp).
 *   Populates summary cards and the per-customer breakdown table.
 */
@WebServlet("/admin/stats")
public class AdminStatsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        Connection con = (Connection) getServletContext()
                .getAttribute("DBConnection");

        AdminDao dao = new AdminDao(con);

        // Platform summary numbers for the stat cards
        int[] summary = dao.getPlatformSummary();
        request.setAttribute("totalCustomers", summary[0]);
        request.setAttribute("totalMessages",  summary[1]);
        request.setAttribute("totalSentToday", summary[2]);

        // Per-customer stats for the breakdown table
        List<CustomerStats> allStats = dao.getAllCustomerStats();
        request.setAttribute("allStats", allStats);

        request.getRequestDispatcher("/adminStats.jsp")
               .forward(request, response);
    }
}
