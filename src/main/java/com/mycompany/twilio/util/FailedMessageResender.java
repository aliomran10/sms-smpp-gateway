package com.mycompany.twilio.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.jsmpp.bean.BindType;
import org.jsmpp.bean.ESMClass;
import org.jsmpp.bean.GeneralDataCoding;
import org.jsmpp.bean.NumberingPlanIndicator;
import org.jsmpp.bean.RegisteredDelivery;
import org.jsmpp.bean.SMSCDeliveryReceipt;
import org.jsmpp.bean.TypeOfNumber;
import org.jsmpp.session.BindParameter;
import org.jsmpp.session.SMPPSession;

public class FailedMessageResender {

    private static final String SMPP_HOST = "127.0.0.1";
    private static final int SMPP_PORT = 2776;
    private static final String SMPP_SYSTEM_ID = "username";
    private static final String SMPP_PASSWORD = "pass1234";
    private static final String SMPP_ADDRESS_RANGE = "6666";
    private static final String DEFAULT_SENDER = "6666";

    public static void main(String[] args) throws Exception {
        while (true) {
            try (Connection con = DatabaseConnectionFactory.getConnection()) {
                List<FailedMessage> failedMessages = loadFailedMessages(con);
                if (failedMessages.isEmpty()) {
                    System.out.println("No failed messages to resend.");
                } else {
                    for (FailedMessage failed : failedMessages) {
                        retryMessage(con, failed);
                    }
                }
            } catch (Exception e) {
                System.err.println("Retry loop failed: " + e.getMessage());
                e.printStackTrace();
            }

            Thread.sleep(Duration.ofSeconds(10).toMillis());
            // Thread.sleep(Duration.ofMinutes(1).toMillis());
        }
    }

    private static List<FailedMessage> loadFailedMessages(Connection con) throws SQLException {
        List<FailedMessage> results = new ArrayList<>();
        String idColumn = findIdColumn(con, "failed_messages");
        String sql = "SELECT " + idColumn + ", msisdn, recipient_no, sender_no, msg FROM failed_messages ORDER BY " + idColumn;

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                FailedMessage message = new FailedMessage();
                message.id = rs.getInt(idColumn);
                message.msisdn = rs.getString("msisdn");
                message.recipientNo = rs.getString("recipient_no");
                message.senderNo = rs.getString("sender_no");
                message.body = rs.getString("msg");
                results.add(message);
            }
        }
        return results;
    }

    private static void retryMessage(Connection con, FailedMessage failed) throws Exception {
        SMPPSession smppSession = new SMPPSession();
        boolean success = false;

        try {
            smppSession.connectAndBind(
                    SMPP_HOST,
                    SMPP_PORT,
                    new BindParameter(
                            BindType.BIND_TRX,
                            SMPP_SYSTEM_ID,
                            SMPP_PASSWORD,
                            "",
                            TypeOfNumber.UNKNOWN,
                            NumberingPlanIndicator.UNKNOWN,
                            SMPP_ADDRESS_RANGE));

            String sender = failed.senderNo != null && !failed.senderNo.isBlank()
                    ? failed.senderNo
                    : DEFAULT_SENDER;

            smppSession.submitShortMessage(
                    "CMT",
                    TypeOfNumber.ALPHANUMERIC,
                    NumberingPlanIndicator.UNKNOWN,
                    sender,
                    TypeOfNumber.INTERNATIONAL,
                    NumberingPlanIndicator.ISDN,
                    failed.recipientNo,
                    new ESMClass(),
                    (byte) 0,
                    (byte) 1,
                    null,
                    null,
                    new RegisteredDelivery(SMSCDeliveryReceipt.SUCCESS_FAILURE),
                    (byte) 0,
                    new GeneralDataCoding(),
                    (byte) 0,
                    failed.body.getBytes(StandardCharsets.UTF_8));

            success = true;
            System.out.println("Resent message id=" + failed.id + " to " + failed.recipientNo);
        } catch (Exception e) {
            System.err.println("Retry failed for message id=" + failed.id + ": " + e.getMessage());
            return;
        } finally {
            try {
                smppSession.unbindAndClose();
            } catch (Exception ignored) {
            }
        }

        if (success) {
            insertIntoMessages(con, failed);
            deleteFromFailedMessages(con, failed.id);
        }
    }

    private static void insertIntoMessages(Connection con, FailedMessage failed) throws SQLException {
        String sql = "INSERT INTO messages (msisdn, recipient_no, sender_no, msg) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            if (failed.msisdn == null || failed.msisdn.isBlank()) {
                ps.setNull(1, Types.VARCHAR);
            } else {
                ps.setString(1, failed.msisdn);
            }
            ps.setString(2, failed.recipientNo);
            ps.setString(3, failed.senderNo);
            ps.setString(4, failed.body);
            ps.executeUpdate();
        }
    }

    private static void deleteFromFailedMessages(Connection con, int id) throws SQLException {
        String idColumn = findIdColumn(con, "failed_messages");
        try (PreparedStatement ps = con.prepareStatement("DELETE FROM failed_messages WHERE " + idColumn + " = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private static String findIdColumn(Connection con, String tableName) throws SQLException {
        DatabaseMetaData metaData = con.getMetaData();
        try (ResultSet columns = metaData.getColumns(null, null, tableName, null)) {
            while (columns.next()) {
                String columnName = columns.getString("COLUMN_NAME");
                if ("id".equalsIgnoreCase(columnName) || "msg_id".equalsIgnoreCase(columnName)) {
                    return columnName;
                }
            }
        }

        return "id";
    }

    private static class FailedMessage {
        private int id;
        private String msisdn;
        private String recipientNo;
        private String senderNo;
        private String body;
    }
}
