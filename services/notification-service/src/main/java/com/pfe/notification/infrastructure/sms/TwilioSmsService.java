package com.pfe.notification.infrastructure.sms;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TwilioSmsService {

    @Value("${twilio.account.sid:#{null}}")
    private String accountSid;

    @Value("${twilio.auth.token:#{null}}")
    private String authToken;

    @Value("${twilio.phone.number:#{null}}")
    private String twilioPhoneNumber;

    @PostConstruct
    public void init() {
        if (accountSid != null && authToken != null) {
            Twilio.init(accountSid, authToken);
            log.info("[TWILIO] SMS service initialized with account SID: {}", 
                accountSid != null ? accountSid.substring(0, 8) + "..." : "N/A");
        } else {
            log.warn("[TWILIO] SMS service not configured - missing credentials");
        }
    }

    public void sendSms(String to, String messageBody) {
        try {
            if (accountSid == null || authToken == null || twilioPhoneNumber == null) {
                log.error("[TWILIO] SMS credentials not configured");
                throw new RuntimeException("Twilio SMS credentials not configured");
            }

            // Format the phone number if needed
            String formattedTo = to.startsWith("+") ? to : "+212" + to.substring(1);
            
            Message message = Message.creator(
                    new PhoneNumber(formattedTo),
                    new PhoneNumber(twilioPhoneNumber),
                    messageBody)
                .create();

            log.info("[TWILIO] SMS sent successfully to={} sid={}", to, message.getSid());
        } catch (Exception e) {
            log.error("[TWILIO] Failed to send SMS to={} : {}", to, e.getMessage(), e);
            throw new RuntimeException("Failed to send SMS: " + e.getMessage(), e);
        }
    }
}
