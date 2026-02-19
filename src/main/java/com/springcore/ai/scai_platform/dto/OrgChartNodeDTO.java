package com.springcore.ai.scai_platform.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OrgChartNodeDTO {
    private Long id;              // employeeId
    private String code;
    private String name;
    private String positionName;
    private String departmentName;
    private List<String> roles;
    private Long managerId;
    private List<OrgChartNodeDTO> children; // สำหรับส่งโครงสร้างแบบ Tree
}

