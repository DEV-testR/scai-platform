package com.springcore.ai.scai_platform.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AIChatRequest {
    private String model;
    private String prompt = "Hi";
}
