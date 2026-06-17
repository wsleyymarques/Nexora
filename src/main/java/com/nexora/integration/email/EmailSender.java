package com.nexora.integration.email;

public interface EmailSender {
    void send(String to, String subject, String body);
}