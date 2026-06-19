package com.nexora.integration.email.impl;

import com.nexora.integration.email.EmailSender;
import com.resend.Resend;
import com.resend.services.emails.model.CreateEmailOptions;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ResendEmailSender implements EmailSender {

    @Value("${nexora.email.api-key}")
    private String apiKey;

    @Value("${nexora.email.from}")
    private String fromAddress;

    private Resend resend;

    @PostConstruct
    private void init() {
        this.resend = new Resend(apiKey);
    }

    @Override
    public void send(String to, String subject, String body) {
        CreateEmailOptions params = CreateEmailOptions.builder()
                .from(fromAddress)
                .to(to)
                .subject(subject)
                .html(body)
                .build();

        try {
            resend.emails().send(params);
            log.info("Email enviado para {}", to);
        } catch (Exception e) {
            log.error("Falha ao enviar email para {}: {}", to, e.getMessage());
            throw new RuntimeException("Falha ao enviar email", e);
        }
    }
}