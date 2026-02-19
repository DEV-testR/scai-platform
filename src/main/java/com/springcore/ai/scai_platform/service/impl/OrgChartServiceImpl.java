package com.springcore.ai.scai_platform.service.impl;

import com.springcore.ai.scai_platform.dto.OrgChartNodeDTO;
import com.springcore.ai.scai_platform.entity.Employee;
import com.springcore.ai.scai_platform.entity.Trafts;
import com.springcore.ai.scai_platform.repository.api.EmployeeHierarchyRepository;
import com.springcore.ai.scai_platform.repository.api.EmployeeRepository;
import com.springcore.ai.scai_platform.repository.api.TraftsRepository;
import com.springcore.ai.scai_platform.service.api.OrgChartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrgChartServiceImpl implements OrgChartService {

    private final TraftsRepository traftsRepository;
    private final EmployeeHierarchyRepository hierarchyRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public List<OrgChartNodeDTO> getOrgChartTree() {
        List<Trafts> allCurrent = traftsRepository.findAllByIscurrentTrue();

        // 1. Map DTOs (Safe mapping)
        Map<Long, OrgChartNodeDTO> nodeMap = allCurrent.stream().collect(Collectors.toMap(
                traft -> traft.getEmployee().getId(),
                this::convertToDTO
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

    @Override
    public List<OrgChartNodeDTO> getRoots() {
        return traftsRepository.findAllByManagerIsNullAndIscurrentTrue()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrgChartNodeDTO> getChildren(Long managerId) {
        // ดึงเฉพาะพนักงานที่มี Manager ID ตามที่ระบุ
        return traftsRepository.findAllByManagerIdAndIscurrentTrue(managerId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrgChartNodeDTO> getUnassignedEmployees() {
        List<Employee> unassigned = employeeRepository.findUnassignedEmployees();
        Set<Long> emIds = unassigned.stream()
                .map(Employee::getId)
                .collect(Collectors.toSet());

        List<Trafts> activeTrafts = traftsRepository.findLatestTraftsByEmployeeIds(emIds);
        Map<Long, Trafts> traftMap = activeTrafts.stream()
                .collect(Collectors.toMap(t -> t.getEmployee().getId(), t -> t));

        return unassigned.stream().map(emp -> {
            Trafts currentTraft = traftMap.get(emp.getId());

            // ดึงตำแหน่งจริงมาโชว์ ถ้าไม่มีถึงจะใช้ "Waiting for positioning."
            String posName = (currentTraft != null && currentTraft.getPosition() != null)
                    ? currentTraft.getPosition().getName()
                    : "Waiting for positioning.";

            return OrgChartNodeDTO.builder()
                    .id(emp.getId()) // อย่าลืมแปลงเป็น String ตามที่หน้าบ้านต้องการ
                    .code(emp.getCode())
                    .name(emp.getName())
                    .positionName(posName)
                    .build();
        }).collect(Collectors.toList());
    }

    // Helper Method สำหรับแปลงเป็น DTO (ลดความซ้ำซ้อนของโค้ด)
    private OrgChartNodeDTO convertToDTO(Trafts traft) {
        OrgChartNodeDTO dto = OrgChartNodeDTO.builder()
                .id(traft.getEmployee().getId())
                .code(traft.getEmployee().getCode())
                .name(traft.getEmployee().getName())
                .positionName(traft.getPosition() != null ? traft.getPosition().getName() : "Waiting for positioning.")
                .build();

        // Recursive: หาลูกน้องของคนนี้ และแปลงเป็น DTO ใส่ใน List children
        List<Trafts> subordinates = traftsRepository.findAllByManagerIdAndIscurrentTrue(traft.getEmployee().getId());
        if (subordinates != null && !subordinates.isEmpty()) {
            dto.setChildren(subordinates.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList()));
        } else {
            dto.setChildren(new ArrayList<>());
        }

        return dto;
    }
}
