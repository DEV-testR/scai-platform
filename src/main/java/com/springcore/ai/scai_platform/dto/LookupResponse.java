package com.springcore.ai.scai_platform.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class LookupResponse {
    private Long id;
    private String code;
    private String name;

    public String getId() {
        return id.toString();
    }
}
