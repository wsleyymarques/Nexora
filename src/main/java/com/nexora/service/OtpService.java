package com.nexora.service;

import com.nexora.exception.BusinessException;
import com.nexora.integration.email.EmailSender;
import com.nexora.integration.otp.OtpSender;
import com.nexora.model.entity.OtpCode;
import com.nexora.model.enums.OtpType;
import com.nexora.repository.OtpCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final OtpCodeRepository otpCodeRepository;
    private final OtpSender otpSender;
    private final EmailSender emailSender;

    @Value("${nexora.otp.expiration-minutes:5}")
    private int expirationMinutes;

    public void sendToPhone(String phone, OtpType type) {

        invalidatePreviousOtp(phone, type);

        OtpCode otp = createOtp(phone, type);

        otpCodeRepository.save(otp);

        otpSender.send(phone, otp.getCode());
    }

    public void sendToEmail(String email, OtpType type) {

        invalidatePreviousOtp(email, type);

        OtpCode otp = createOtp(email, type);

        otpCodeRepository.save(otp);

        emailSender.send(
                email,
                "Seu código de verificação",
                "Código: " + otp.getCode()
        );
    }

    public void validate(String target, String code, OtpType type) {

        OtpCode otp = otpCodeRepository
                .findTopByTargetAndTypeAndUsedFalseOrderByCreatedAtDesc(target, type)
                .orElseThrow(() ->
                        new BusinessException(
                                "Código não encontrado",
                                HttpStatus.NOT_FOUND
                        )
                );

        if (otp.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException(
                    "Código expirado",
                    HttpStatus.BAD_REQUEST
            );
        }

        if (!otp.getCode().equals(code)) {
            throw new BusinessException(
                    "Código inválido",
                    HttpStatus.BAD_REQUEST
            );
        }

        otp.setUsed(true);
        otpCodeRepository.save(otp);
    }

    private OtpCode createOtp(String target, OtpType type) {
        return OtpCode.builder()
                .target(target)
                .code(generateCode())
                .type(type)
                .expiresAt(LocalDateTime.now().plusMinutes(expirationMinutes))
                .build();
    }

    private void invalidatePreviousOtp(String target, OtpType type) {

        otpCodeRepository
                .findTopByTargetAndTypeAndUsedFalseOrderByCreatedAtDesc(target, type)
                .ifPresent(otp -> {
                    otp.setUsed(true);
                    otpCodeRepository.save(otp);
                });

    }

    private String generateCode() {
        SecureRandom random = new SecureRandom();
        int code = random.nextInt(999999);
        return String.format("%06d", code);
    }
}