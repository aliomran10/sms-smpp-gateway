/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.twilio;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;


/**
 *
 * @author roqaya
 */


public class TwilioService {

    public static Message sendSMS(
            String accountSid,
            String authToken,
            String from,
            String to,
            String body) {

        // Initialize Twilio
        Twilio.init(accountSid, authToken);

        // Create and send message
        Message message = Message.creator(
                new PhoneNumber(to),
                new PhoneNumber(from),
                body
        ).create();

        return message;
    }
}
