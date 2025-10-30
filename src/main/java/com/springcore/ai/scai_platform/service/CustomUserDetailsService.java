package com.springcore.ai.scai_platform.service;

import com.springcore.ai.scai_platform.dto.UserPrincipal;
import com.springcore.ai.scai_platform.entity.User;
import com.springcore.ai.scai_platform.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        /*return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities("ROLE_USER") // กำหนด role เป็น USER, ปรับได้ตามระบบ
                .build();*/
        return UserPrincipal.create(user);  // เรียก factory method แทน new ตรงๆ
    }
}