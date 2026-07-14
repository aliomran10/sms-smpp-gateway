package com.mycompany.twilio.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.TimeZone;

public final class DatabaseConnectionFactory {

    private static final String DEFAULT_URL =
            "jdbc:postgresql://ep-dawn-base-ag3iq1vt-pooler.c-2.eu-central-1.aws.neon.tech/Twilio-SMS-Management?sslmode=require&channelBinding=require";
    private static final String DEFAULT_USER = "neondb_owner";
    private static final String DEFAULT_PASSWORD = "npg_jwyQ23SJiUoz";
    private static final String APP_TIME_ZONE = "Africa/Cairo";

    static {
        TimeZone.setDefault(TimeZone.getTimeZone(APP_TIME_ZONE));
    }

    private DatabaseConnectionFactory() {
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("PostgreSQL JDBC driver not found", e);
        }

        String url = System.getenv().getOrDefault("DB_URL", DEFAULT_URL);
        String user = System.getenv().getOrDefault("DB_USER", DEFAULT_USER);
        String password = System.getenv().getOrDefault("DB_PASSWORD", DEFAULT_PASSWORD);

        Connection connection = DriverManager.getConnection(url, user, password);
        configureTimeZone(connection);
        return connection;
    }

    private static void configureTimeZone(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("SET TIME ZONE '" + APP_TIME_ZONE + "'");
        }
    }
}
