/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.twilio;

import java.sql.Timestamp;

/**
 *
 * @author mohamed
 */
public class Message {

    private int msgId;
    private String senderNo;
    private String recipientNo;
    private String msg;
    private Timestamp sentAt;

    public int getMsgId() {
        return msgId;
    }

    public void setMsgId(int msgId) {
        this.msgId = msgId;
    }

    public String getSenderNo() {
        return senderNo;
    }

    public void setSenderNo(String senderNo) {
        this.senderNo = senderNo;
    }

    public String getRecipientNo() {
        return recipientNo;
    }

    public void setRecipientNo(String recipientNo) {
        this.recipientNo = recipientNo;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Timestamp getSentAt() {
        return sentAt;
    }

    public void setSentAt(Timestamp sentAt) {
        this.sentAt = sentAt;
    }
}
