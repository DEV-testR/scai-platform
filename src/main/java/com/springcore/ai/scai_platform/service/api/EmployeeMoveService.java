package com.springcore.ai.scai_platform.service.api;

public interface EmployeeMoveService {
    /**
     * ย้ายตำแหน่งพนักงาน (Drag & Drop)
     * @param employeeId ID ของพนักงานที่ถูกลาก
     * @param newManagerId ID ของหัวหน้าคนใหม่
     */
    void moveEmployee(Long employeeId, Long newManagerId);
}
