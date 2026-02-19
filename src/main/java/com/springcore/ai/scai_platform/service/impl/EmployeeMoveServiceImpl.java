package com.springcore.ai.scai_platform.service.impl;

import com.springcore.ai.scai_platform.entity.Employee;
import com.springcore.ai.scai_platform.entity.Trafts;
import com.springcore.ai.scai_platform.repository.api.EmployeeHierarchyRepository;
import com.springcore.ai.scai_platform.repository.api.EmployeeRepository;
import com.springcore.ai.scai_platform.repository.api.TraftsRepository;
import com.springcore.ai.scai_platform.service.api.EmployeeMoveService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EmployeeMoveServiceImpl implements EmployeeMoveService {

    private final TraftsRepository traftsRepository;
    private final EmployeeHierarchyRepository hierarchyRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional
    public void moveEmployee(Long employeeId, Long newManagerId) {
        // 1. ป้องกันการลากวางทับตัวเอง
        if (employeeId.equals(newManagerId)) {
            throw new RuntimeException("ไม่สามารถย้ายพนักงานไปเป็นหัวหน้าของตัวเองได้");
        }

        // 2. ตรวจสอบ Circular Reference (ป้องกันการลากหัวหน้าไปไว้ใต้ลูกน้องตัวเอง)
        // ถ้า "พนักงานที่จะย้าย" เป็น "หัวหน้า" (ไม่ว่าจะระดับไหน) ของ "หัวหน้าใหม่" = ห้ามย้าย!
        boolean isSubordinate = hierarchyRepository.existsByIdAncestoridAndIdDescendantid(employeeId, newManagerId);
        if (isSubordinate) {
            throw new RuntimeException("ไม่สามารถย้ายหัวหน้าไปอยู่ใต้สายบังคับบัญชาของลูกน้องตัวเองได้ (Circular Reference Detected)");
        }

        LocalDateTime now = LocalDateTime.now();

        // 3. ดึงข้อมูลพนักงานและหัวหน้าใหม่
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("ไม่พบข้อมูลพนักงาน"));
        Employee newManager = employeeRepository.findById(newManagerId)
                .orElseThrow(() -> new RuntimeException("ไม่พบข้อมูลหัวหน้าคนใหม่"));

        // 4. จัดการ Trafts (History & Point-in-time)
        Trafts currentTraft = traftsRepository.findByEmployeeIdAndIscurrentTrue(employeeId)
                .orElseThrow(() -> new RuntimeException("ไม่พบสถานะปัจจุบันของพนักงาน"));

        currentTraft.setIscurrent(false);
        currentTraft.setEnddate(now);
        traftsRepository.save(currentTraft);

        Trafts newTraft = Trafts.builder()
                .employee(employee)
                .manager(newManager)
                .department(currentTraft.getDepartment())
                .position(currentTraft.getPosition())
                .jobRoles(new java.util.HashSet<>(currentTraft.getJobRoles()))
                .effectivedate(now)
                .iscurrent(true)
                .trafttype("ORG_CHART_MOVE")
                .build();
        traftsRepository.save(newTraft);

        // 5. อัปเดต Closure Table (Bulk Update กิ่งไม้ทั้งสาย)
        hierarchyRepository.deleteOldHierarchy(employeeId);
        hierarchyRepository.insertNewHierarchy(employeeId, newManagerId);
    }

}
