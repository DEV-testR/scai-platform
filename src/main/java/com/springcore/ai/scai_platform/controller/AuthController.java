package com.springcore.ai.scai_platform.controller;

import com.springcore.ai.scai_platform.dto.AuthResponse;
import com.springcore.ai.scai_platform.dto.LoginRequest;
import com.springcore.ai.scai_platform.dto.RegisterRequest;
import com.springcore.ai.scai_platform.dto.ResetPasswordRequest;
import com.springcore.ai.scai_platform.entity.User;
import com.springcore.ai.scai_platform.service.api.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        AuthResponse authResponse = authService.login(request);
        long refreshTokenExpirationMs = authResponse.getRefreshTokenExpirationMs();
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", authResponse.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/api/auth/refresh")
                .maxAge(refreshTokenExpirationMs)
                .sameSite("Strict")
                .build();

        response.setHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/reset-password-default")
    public ResponseEntity<?> resetToDefault(@RequestBody ResetPasswordRequest request) {
        try {
            String username = request.getEmail();
            authService.resetPassword(username);
            return ResponseEntity.ok("Password has been reset to default for: " + username);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/login/pin")
    public ResponseEntity<AuthResponse> loginWithPin(@RequestParam String email, @RequestParam String pin) {
        AuthResponse authResponse = authService.loginWithPin(email, pin);
        return ResponseEntity.ok(authResponse);
    }

    @GetMapping("/email/validate")
    public ResponseEntity<Void> validateEmail(@RequestParam String email) {
        boolean isExistsMail = authService.validateEmail(email);
        if (isExistsMail) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@CookieValue(name = "refreshToken", required = false) String refreshToken) {
        if (refreshToken == null || authService.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(AuthResponse.builder()
                .accessToken(authService.refreshToken(refreshToken))
                .build());
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        if (refreshToken == null || authService.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String newAccessToken = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(AuthResponse.builder()
                .accessToken(newAccessToken)
                .build());
    }

    // logout ลบ cookie refresh token
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/api/auth/refresh")
                .maxAge(0)
                .sameSite("Strict")
                .build();
        response.setHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());

        return ResponseEntity.ok().build();
    }
}

