/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/ServletListener.java to edit this template
 */
package com.mycompany.twilio.util;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.TimeZone;
import jakarta.servlet.ServletContext;

/**
 * Web application lifecycle listener.
 *
 * @author omar
 */
public class DBConnectionInitializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            TimeZone.setDefault(TimeZone.getTimeZone("Africa/Cairo"));
            Class.forName("org.postgresql.Driver");
            Connection con = DriverManager.getConnection(
                    "jdbc:postgresql://ep-dawn-base-ag3iq1vt-pooler.c-2.eu-central-1.aws.neon.tech/Twilio-SMS-Management?sslmode=require&channelBinding=require",
                    "neondb_owner", "npg_jwyQ23SJiUoz");
            try (Statement statement = con.createStatement()) {
                statement.execute("SET TIME ZONE 'Africa/Cairo'");
            }
            ServletContext ctx = sce.getServletContext();
            ctx.setAttribute("DBConnection", con);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        try {
            Connection con = (Connection) sce.getServletContext().getAttribute("DBConnection");
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
