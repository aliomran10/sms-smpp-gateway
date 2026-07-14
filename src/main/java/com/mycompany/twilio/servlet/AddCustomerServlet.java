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
 * GET /admin/add-customer → show the add-customer form (adminAddCustomer.jsp)
 * POST /admin/add-customer → persist the new customer, redirect to customer
 * list
 *
 * Admin-created accounts are immediately verified; no OTP flow is triggered.
 */
@WebServlet("/admin/add-customer")
public class AddCustomerServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher("/adminAddCustomer.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String fullName = request.getParameter("fullName");
        String birthday = request.getParameter("birthday");
        String job = request.getParameter("job");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String msisdn = request.getParameter("msisdn");
        String physicalAddress = request.getParameter("physicalAddress");
        String twilioSid = request.getParameter("twilioSid");
        String twilioToken = request.getParameter("twilioToken");
        String twilioSender = request.getParameter("twilioSender");
        
                // If twilioSender is empty, default it to the customer's MSISDN
                if ((twilioSender == null || twilioSender.isBlank()) && msisdn != null && !msisdn.isBlank()) {
                        twilioSender = msisdn;
                }
        // Basic server-side validation
        if (fullName == null || fullName.isBlank()
                || email == null || email.isBlank()
                || password == null || password.isBlank()) {
            request.setAttribute("error", "Full name, e-mail, and password are required.");
            request.getRequestDispatcher("/adminAddCustomer.jsp")
                    .forward(request, response);
            return;
        }

        User user = new User();
        user.setFullName(fullName);
        user.setBirthday(birthday);
        user.setJob(job);
        user.setEmail(email);
        user.setMsisdn(msisdn);
        user.setPhysicalAddress(physicalAddress);
        user.setTwilioAccountSid(twilioSid);
        user.setTwilioAuthToken(twilioToken);
        user.setTwilioSenderId(twilioSender);

        Connection con = (Connection) getServletContext()
                .getAttribute("DBConnection");

        AdminDao dao = new AdminDao(con);
        int newId = dao.addCustomer(user, password);

        if (newId > 0) {
            response.sendRedirect(request.getContextPath()
                    + "/admin/customers?msg=Customer+added+successfully");
        } else {
            request.setAttribute("error",
                    "Failed to create customer. The e-mail or MSISDN may already be in use.");
            request.getRequestDispatcher("/adminAddCustomer.jsp")
                    .forward(request, response);
        }
    }
}
