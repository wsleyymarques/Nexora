package com.nexora.integration.email.impl;

import com.nexora.integration.email.EmailSender;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("prod")
public class SmtpEmailSender implements EmailSender {

    @Override
    public void send(String to, String subject, String body) {
        throw new UnsupportedOperationException("SMTP não implementado ainda");
    }
}