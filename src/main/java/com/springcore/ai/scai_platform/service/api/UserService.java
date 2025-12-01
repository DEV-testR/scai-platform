package com.springcore.ai.scai_platform.service.api;

import com.springcore.ai.scai_platform.dto.UpdateProfileRequest;
import com.springcore.ai.scai_platform.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {

    void logout(String token);

    User updateProfile(Long userId, UpdateProfileRequest request);

    User getCurrentUser(Long id);

    List<User> getUsers();
}


