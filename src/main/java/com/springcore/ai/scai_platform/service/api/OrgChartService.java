package com.springcore.ai.scai_platform.service.api;

import com.springcore.ai.scai_platform.dto.OrgChartNodeDTO;

import java.util.List;

public interface OrgChartService {
    List<OrgChartNodeDTO> getOrgChartTree();
    List<Long> getPathToRoot(Long employeeId);
}
