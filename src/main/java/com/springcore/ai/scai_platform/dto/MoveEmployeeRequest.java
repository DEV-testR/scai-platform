package com.springcore.ai.scai_platform.dto;

import lombok.Data;

@Data
public class MoveEmployeeRequest {
    private Long employeeId;
    private Long newManagerId;
}
