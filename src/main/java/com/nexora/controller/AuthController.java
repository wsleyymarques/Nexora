package com.nexora.controller;
import com.nexora.dto.request.*;
import com.nexora.dto.response.AuthResponse;
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
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody StoreRegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody StoreLoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
    // fluxo customer
    @PostMapping("/customer/register")
    public ResponseEntity<Void> customerRegister(@Valid @RequestBody CustomerRegisterRequest request) {
        authService.registerCustomer(request);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/customer/login")
    public ResponseEntity<?> customerLogin(@Valid @RequestBody CustomerLoginRequest request) {
        return ResponseEntity.ok(authService.loginCustomer(request));
    }

    @PostMapping("/customer/verify-otp")
    public ResponseEntity<AuthResponse> verifyOtp(@Valid @RequestBody OtpValidateRequest request) {
        return ResponseEntity.ok(authService.verifyOtp(request));
    }
}
