/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.twilio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mohamed
 */
public class MessageDao {

    private Connection con;

    public MessageDao(Connection con) {
        this.con = con;
    }

    public List<Message> searchMessages(
            String msisdn,
            String keyword,
            String fromDate,
            String toDate
    ) throws Exception {

        List<Message> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder("""
        SELECT *
        FROM messages
        WHERE sender_no = ? 
        OR recipient_no =?
    """);

        List<Object> params = new ArrayList<>();

        params.add(msisdn);
        params.add(msisdn);
        // keyword search
        if (keyword != null && !keyword.trim().isEmpty()) {

            sql.append("""
            AND (
                msg ILIKE ?
                OR sender_no ILIKE ?
                OR recipient_no ILIKE ?
            )
        """);

            String search = "%" + keyword + "%";

            params.add(search);
            params.add(search);
            params.add(search);
        }

        // from date
        if (fromDate != null && !fromDate.isEmpty()) {

            sql.append(" AND sent_at >= ? ");

            params.add(
                    Timestamp.valueOf(
                            fromDate.replace("T", " ") + ":00"
                    )
            );
        }

        // to date
        if (toDate != null && !toDate.isEmpty()) {

            sql.append(" AND sent_at <= ? ");

            params.add(
                    Timestamp.valueOf(
                            toDate.replace("T", " ") + ":00"
                    )
            );
        }

        sql.append(" ORDER BY sent_at DESC");

        PreparedStatement ps
                = con.prepareStatement(sql.toString());

        for (int i = 0; i < params.size(); i++) {

            ps.setObject(i + 1, params.get(i));
        }

        ResultSet rs = ps.executeQuery();

        while (rs.next()) {

            Message m = new Message();

            m.setMsgId(rs.getInt("msg_id"));
            m.setSenderNo(rs.getString("sender_no"));
            m.setRecipientNo(rs.getString("recipient_no"));
            m.setMsg(rs.getString("msg"));
            m.setSentAt(rs.getTimestamp("sent_at"));

            list.add(m);
        }

        return list;
    }

    public boolean deleteMessage(
            int msgId
    ) throws Exception {

        String sql = """
                        DELETE FROM messages
                        WHERE msg_id = ?
                      """;

        PreparedStatement ps = con.prepareStatement(sql);

        ps.setInt(1, msgId);

        int rows = ps.executeUpdate();

        return rows > 0;
    }
}
