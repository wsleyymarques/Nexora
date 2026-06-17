package com.nexora.integration.otp.impl;

import com.nexora.integration.otp.OtpSender;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("prod")
public class EvolutionApiOtpSender implements OtpSender {

    @Override
    public void send(String phone, String code) {
        throw new UnsupportedOperationException("Evolution API não implementada ainda");
    }
}