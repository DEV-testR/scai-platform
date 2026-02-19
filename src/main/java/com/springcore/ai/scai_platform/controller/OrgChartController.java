package com.springcore.ai.scai_platform.controller;

import com.springcore.ai.scai_platform.dto.MoveEmployeeRequest;
import com.springcore.ai.scai_platform.dto.OrgChartNodeDTO;
import com.springcore.ai.scai_platform.service.api.EmployeeMoveService;
import com.springcore.ai.scai_platform.service.api.OrgChartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/org-chart")
@RequiredArgsConstructor
public class OrgChartController {

    private final EmployeeMoveService moveService;
    private final OrgChartService orgChartService;

    /**
     * สำหรับวาดผังองค์กรทั้งหมด
     */
    @GetMapping("/tree")
    public ResponseEntity<List<OrgChartNodeDTO>> getFullTree() {
        return ResponseEntity.ok(orgChartService.getOrgChartTree());
    }

    /**
     * สำหรับรับ Event การ Drag & Drop จากหน้าจอ
     */
    @PostMapping("/move")
    public ResponseEntity<String> moveEmployee(@RequestBody MoveEmployeeRequest request) {
        try {
            moveService.moveEmployee(request.getEmployeeId(), request.getNewManagerId());
            return ResponseEntity.ok("ย้ายตำแหน่งพนักงานเรียบร้อยแล้ว");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 3. หาตำแหน่งพนักงาน (เพื่อไฮไลท์ในผัง)
    @GetMapping("/path/{id}")
    public ResponseEntity<List<Long>> getPath(@PathVariable Long id) {
        return ResponseEntity.ok(orgChartService.getPathToRoot(id));
    }

}
