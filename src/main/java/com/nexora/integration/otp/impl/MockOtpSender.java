package com.nexora.integration.otp.impl;

import com.nexora.integration.otp.OtpSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("dev")
public class MockOtpSender implements OtpSender {

    @Override
    public void send(String phone, String code) {
        log.info("[OTP MOCK] Telefone: {} | Código: {}", phone, code);
    }
}