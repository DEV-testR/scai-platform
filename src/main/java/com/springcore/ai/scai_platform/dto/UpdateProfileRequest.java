package com.springcore.ai.scai_platform.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UpdateProfileRequest {
    private String fullName;
    private String phone;
    private String address;
    private String profilePictureUrl;
}
