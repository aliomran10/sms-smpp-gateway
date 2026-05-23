package com.mycompany.twilio.servlet;

import com.mycompany.twilio.model.User;
import com.mycompany.twilio.dao.AdminDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;

/**
 *
 * @author Ali
 */
/**
 * GET /admin/customers
 *   Renders the customer list page (adminCustomers.jsp).
 *   Supports an optional ?search= query parameter that filters by name,
 *   e-mail, or MSISDN.
 */
@WebServlet("/admin/customers")
public class ListCustomersServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        Connection con = (Connection) getServletContext()
                .getAttribute("DBConnection");

        AdminDao dao = new AdminDao(con);

        // Full customer list (search filtering done in JSP via EL for simplicity;
        // for large datasets, move the WHERE clause into AdminDao.searchCustomers())
        List<User> customers = dao.getAllCustomers();

        // Platform summary numbers for the stats bar at the top
        int[] summary = dao.getPlatformSummary();

        request.setAttribute("customers",        customers);
        request.setAttribute("totalCustomers",   summary[0]);
        request.setAttribute("totalMessages",    summary[1]);
        request.setAttribute("totalSentToday",   summary[2]);

        // Pass any flash message from a redirect (e.g. after delete/add)
        String flash = request.getParameter("msg");
        if (flash != null) request.setAttribute("flashMsg", flash);

        request.getRequestDispatcher("/adminCustomers.jsp")
               .forward(request, response);
    }
}
