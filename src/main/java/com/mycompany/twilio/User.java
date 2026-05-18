/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.twilio;

/**
 *
 * @author mohamed
 */

public class User {

    private int userId;

    private String fullName;

    private String birthday;

    private String job;

    private String email;

    private String msisdn;

    private String physicalAddress;

    private String twilioAccountSid;

    private String twilioAuthToken;

    private String twilioSenderId;

    public int getUserId() {

        return userId;
    }

    public void setUserId(int userId) {

        this.userId = userId;
    }

    public String getFullName() {

        return fullName;
    }

    public void setFullName(String fullName) {

        this.fullName = fullName;
    }

    public String getBirthday() {

        return birthday;
    }

    public void setBirthday(String birthday) {

        this.birthday = birthday;
    }

    public String getJob() {

        return job;
    }

    public void setJob(String job) {

        this.job = job;
    }

    public String getEmail() {

        return email;
    }

    public void setEmail(String email) {

        this.email = email;
    }

    public String getMsisdn() {

        return msisdn;
    }

    public void setMsisdn(String msisdn) {

        this.msisdn = msisdn;
    }

    public String getPhysicalAddress() {

        return physicalAddress;
    }

    public void setPhysicalAddress(
            String physicalAddress
    ) {

        this.physicalAddress =
                physicalAddress;
    }

    public String getTwilioAccountSid() {

        return twilioAccountSid;
    }

    public void setTwilioAccountSid(
            String twilioAccountSid
    ) {

        this.twilioAccountSid =
                twilioAccountSid;
    }

    public String getTwilioAuthToken() {

        return twilioAuthToken;
    }

    public void setTwilioAuthToken(
            String twilioAuthToken
    ) {

        this.twilioAuthToken =
                twilioAuthToken;
    }

    public String getTwilioSenderId() {

        return twilioSenderId;
    }

    public void setTwilioSenderId(
            String twilioSenderId
    ) {

        this.twilioSenderId =
                twilioSenderId;
    }
}
