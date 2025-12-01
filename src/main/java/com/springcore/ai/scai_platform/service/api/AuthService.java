package com.springcore.ai.scai_platform.service.api;

import com.springcore.ai.scai_platform.dto.LoginRequest;
import com.springcore.ai.scai_platform.dto.AuthResponse;
import com.springcore.ai.scai_platform.dto.RegisterRequest;
import com.springcore.ai.scai_platform.entity.User;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    User register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse loginWithPin(String email, String pin);

    String refreshToken(String refreshToken);

    boolean validateEmail(String email);

    boolean validateToken(String token);
}


