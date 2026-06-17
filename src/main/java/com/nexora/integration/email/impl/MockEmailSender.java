package com.nexora.integration.email.impl;

import com.nexora.integration.email.EmailSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("dev")
public class MockEmailSender implements EmailSender {

    @Override
    public void send(String to, String subject, String body) {
        log.info("[EMAIL MOCK] Para: {} | Assunto: {} | Corpo: {}", to, subject, body);
    }
}