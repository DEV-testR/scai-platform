package com.springcore.ai.scai_platform.service.impl;

import com.springcore.ai.scai_platform.dto.OrgChartNodeDTO;
import com.springcore.ai.scai_platform.entity.JobRole;
import com.springcore.ai.scai_platform.entity.Trafts;
import com.springcore.ai.scai_platform.repository.api.EmployeeHierarchyRepository;
import com.springcore.ai.scai_platform.repository.api.TraftsRepository;
import com.springcore.ai.scai_platform.service.api.OrgChartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrgChartServiceImpl implements OrgChartService {

    private final TraftsRepository traftsRepository;
    private final EmployeeHierarchyRepository hierarchyRepository;

    @Override
    public List<OrgChartNodeDTO> getOrgChartTree() {
        List<Trafts> allCurrent = traftsRepository.findAllByIscurrentTrue();

        // 1. Map DTOs (Safe mapping)
        Map<Long, OrgChartNodeDTO> nodeMap = allCurrent.stream().collect(Collectors.toMap(
                traft -> traft.getEmployee().getId(),
                traft -> OrgChartNodeDTO.builder()
                        .id(traft.getEmployee().getId())
                        .code(traft.getEmployee().getCode())
                        .name(traft.getEmployee().getName())
                        .positionName(traft.getPosition() != null ? traft.getPosition().getName() : "-")
                        .departmentName(traft.getDepartment() != null ? traft.getDepartment().getName() : "-")
                        .managerId(traft.getManager() != null ? traft.getManager().getId() : null)
                        .roles(traft.getJobRoles().stream().map(JobRole::getRoleName).collect(Collectors.toList()))
                        .children(new ArrayList<>())
                        .build()
        ));

        List<OrgChartNodeDTO> rootNodes = new ArrayList<>();

        // 2. Build Tree & Handle Orphans
        nodeMap.values().forEach(node -> {
            OrgChartNodeDTO parent = (node.getManagerId() != null) ? nodeMap.get(node.getManagerId()) : null;

            if (parent == null) {
                // ถ้าไม่มีหัวหน้า หรือหัวหน้าไม่มีตัวตนในระบบปัจจุบัน ให้ถือว่าเป็น Root (เพื่อไม่ให้คนหาย)
                rootNodes.add(node);
            } else {
                parent.getChildren().add(node);
            }
        });

        return rootNodes;
    }

    @Override
    public List<Long> getPathToRoot(Long employeeId) {
        // จะได้ List ของ ID เช่น [1, 5, 10, 22] (CEO -> ... -> พนักงาน)
        return hierarchyRepository.getAncestorIds(employeeId);
    }
}
