package com.nexora.controller;
import com.nexora.dto.request.*;
import com.nexora.dto.response.AuthResponse;
import com.nexora.audit.Auditable;
import com.nexora.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    @Auditable(action = "USER_REGISTERED", entityType = "USER")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody StoreRegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    @Auditable(action = "USER_LOGGED_IN", entityType = "USER")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody StoreLoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
    // fluxo customer
    @PostMapping("/customer/register")
    @Auditable(action = "CUSTOMER_REGISTERED", entityType = "USER")
    public ResponseEntity<Void> customerRegister(@Valid @RequestBody CustomerRegisterRequest request) {
        authService.registerCustomer(request);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/customer/login")
    @Auditable(action = "CUSTOMER_LOGIN", entityType = "USER")
    public ResponseEntity<?> customerLogin(@Valid @RequestBody CustomerLoginRequest request) {
        return ResponseEntity.ok(authService.loginCustomer(request));
    }

    @PostMapping("/customer/verify-otp")
    @Auditable(action = "CUSTOMER_VERIFIED", entityType = "USER")
    public ResponseEntity<AuthResponse> verifyOtp(@Valid @RequestBody OtpValidateRequest request) {
        return ResponseEntity.ok(authService.verifyOtp(request));
    }

    @PostMapping("/forgot-password")
    @Auditable(action = "PASSWORD_RESET_REQUESTED", entityType = "USER")
    public ResponseEntity<Void> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {

        authService.forgotPassword(request);

        return ResponseEntity.accepted().build();
    }

    @PostMapping("/reset-password")
    @Auditable(action = "PASSWORD_RESET", entityType = "USER")
    public ResponseEntity<Void> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {

        authService.resetPassword(request);

        return ResponseEntity.noContent().build();
    }
}
