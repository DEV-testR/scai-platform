package com.springcore.ai.scai_platform.service;

import com.springcore.ai.scai_platform.dto.UserPrincipal;
import com.springcore.ai.scai_platform.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ScaiUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public ScaiUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        return UserPrincipal.create(userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email)));  // เรียก factory method แทน new ตรงๆ
    }
}