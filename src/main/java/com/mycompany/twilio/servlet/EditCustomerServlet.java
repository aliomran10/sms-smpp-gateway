package com.mycompany.twilio.servlet;

import com.mycompany.twilio.model.User;
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
 * GET  /admin/edit-customer?id={userId}  → show pre-filled edit form
 * POST /admin/edit-customer              → apply changes, redirect to list
 */
@WebServlet("/admin/edit-customer")
public class EditCustomerServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/admin/customers");
            return;
        }

        int userId;
        try {
            userId = Integer.parseInt(idParam.trim());
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/customers");
            return;
        }

        Connection con = (Connection) getServletContext()
                .getAttribute("DBConnection");

        AdminDao dao  = new AdminDao(con);
        User customer = dao.getCustomerById(userId);

        if (customer == null) {
            response.sendRedirect(request.getContextPath()
                    + "/admin/customers?msg=Customer+not+found");
            return;
        }

        request.setAttribute("customer", customer);
        request.getRequestDispatcher("/adminEditCustomer.jsp")
               .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("userId");
        if (idParam == null || idParam.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/admin/customers");
            return;
        }

        int userId;
        try {
            userId = Integer.parseInt(idParam.trim());
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/customers");
            return;
        }

        User user = new User();
        user.setUserId(userId);
        user.setFullName(request.getParameter("fullName"));
        user.setBirthday(request.getParameter("birthday"));
        user.setJob(request.getParameter("job"));
        user.setEmail(request.getParameter("email"));
        user.setMsisdn(request.getParameter("msisdn"));
        user.setPhysicalAddress(request.getParameter("physicalAddress"));
        user.setTwilioAccountSid(request.getParameter("twilioSid"));
        user.setTwilioAuthToken(request.getParameter("twilioToken"));   // blank → keep old
        user.setTwilioSenderId(request.getParameter("twilioSender"));

        Connection con = (Connection) getServletContext()
                .getAttribute("DBConnection");

        AdminDao dao   = new AdminDao(con);
        boolean updated = dao.updateCustomer(user);

        if (updated) {
            response.sendRedirect(request.getContextPath()
                    + "/admin/customers?msg=Customer+updated+successfully");
        } else {
            request.setAttribute("customer", user);
            request.setAttribute("error", "Update failed. Please check the values and try again.");
            request.getRequestDispatcher("/adminEditCustomer.jsp")
                   .forward(request, response);
        }
    }
}
