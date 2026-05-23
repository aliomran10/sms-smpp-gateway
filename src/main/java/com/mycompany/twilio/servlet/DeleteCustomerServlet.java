package com.mycompany.twilio.servlet;

import com.mycompany.twilio.dao.AdminDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;

/**
 *
 * @author Ali
 */
/**
 * POST /admin/delete-customer Deletes a customer (non-admin user) by user_id.
 * All messages associated with the customer's MSISDN are removed automatically
 * via the ON DELETE CASCADE constraint.
 *
 * Expects a form field: userId (int)
 */
@WebServlet("/admin/delete-customer")
public class DeleteCustomerServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("userId");

        if (idParam == null || idParam.isBlank()) {
            response.sendRedirect(request.getContextPath()
                    + "/admin/customers?msg=Invalid+request");
            return;
        }

        int userId;
        try {
            userId = Integer.parseInt(idParam.trim());
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath()
                    + "/admin/customers?msg=Invalid+customer+ID");
            return;
        }

        Connection con = (Connection) getServletContext()
                .getAttribute("DBConnection");

        AdminDao dao = new AdminDao(con);
        boolean deleted = dao.deleteCustomer(userId);

        if (deleted) {
            response.sendRedirect(request.getContextPath()
                    + "/admin/customers?msg=Customer+deleted+successfully");
        } else {
            response.sendRedirect(request.getContextPath()
                    + "/admin/customers?msg=Delete+failed:+customer+not+found");
        }
    }
}
