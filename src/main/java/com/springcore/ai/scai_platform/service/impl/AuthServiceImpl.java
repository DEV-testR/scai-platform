package com.springcore.ai.scai_platform.service.impl;

import com.springcore.ai.scai_platform.dto.LoginRequest;
import com.springcore.ai.scai_platform.dto.AuthResponse;
import com.springcore.ai.scai_platform.dto.RegisterRequest;
import com.springcore.ai.scai_platform.entity.User;
import com.springcore.ai.scai_platform.repository.api.UserRepository;
import com.springcore.ai.scai_platform.security.jwt.JwtTokenProvider;
import com.springcore.ai.scai_platform.service.api.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    @Override
    public User register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPin(passwordEncoder.encode("000000"));
        // user.setFullName(request.getFullName());
        // user.setSocialProvider(request.getSocialProvider());
        // user.setSocialId(request.getSocialId());
        return userRepository.save(user);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        // authenticate user; จะโยน exception ถ้าไม่ผ่าน
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String email = user.getEmail();
        String accessToken = jwtTokenProvider.generateAccessToken(email);
        String refreshToken = jwtTokenProvider.generateRefreshToken(email);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .refreshTokenExpirationMs(jwtTokenProvider.getRefreshTokenExpirationMs())
                .build();
    }

    @Override
    public void resetPassword(String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        String defaultPassword = "@rgenT1";
        user.setPassword(passwordEncoder.encode(defaultPassword));
        userRepository.save(user);
    }

    @Override
    public AuthResponse loginWithPin(String email, String pin) {
        // 1. ค้นหาผู้ใช้ด้วยอีเมล
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Invalid Email")); // ใช้ BadCredentialsException เพื่อให้สอดคล้องกับ Spring Security

        // 2. ตรวจสอบ PIN (สมมติว่า PIN ถูกเก็บแบบเข้ารหัส)
        // คุณต้องแน่ใจว่า field 'pin' ใน User entity ของคุณเป็น String
        // และคุณมี PasswordEncoder ที่ใช้เข้ารหัส/ถอดรหัส PIN
        if (!passwordEncoder.matches(pin, user.getPin())) { // สมมติว่ามี getPin() ใน User entity
            throw new BadCredentialsException("Invalid email or PIN.");
        }

        // 3. สร้าง Access Token และ Refresh Token
        String accessToken = jwtTokenProvider.generateAccessToken(email);
        String refreshToken = jwtTokenProvider.generateRefreshToken(email);

        // 4. คืน AuthResponse
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .refreshTokenExpirationMs(jwtTokenProvider.getRefreshTokenExpirationMs())
                .build();
    }

    @Override
    public boolean validateEmail(String email) {
        // ตรวจสอบว่ามีผู้ใช้ที่อีเมลนี้อยู่ในระบบหรือไม่
        // findByEmail คืน Optional<User> ดังนั้น existsByEmail จะชัดเจนกว่า
        return userRepository.findByEmail(email).isPresent();
        // หรือถ้ามี method existsByEmail ใน UserRepository:
        // return userRepository.existsByEmail(email);
    }

    @Override
    public String refreshToken(String refreshToken) {
        String email = jwtTokenProvider.getEmailFromToken(refreshToken);
        return jwtTokenProvider.generateAccessToken(email);
    }

    @Override
    public boolean validateToken(String token) {
        return jwtTokenProvider.validateToken(token);
    }
}
