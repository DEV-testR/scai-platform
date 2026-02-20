package com.springcore.ai.scai_platform.service.api;

public interface EmployeeMoveService {
    void moveEmployee(Long employeeId, Long newManagerId);
    void unassignEmployee(Long employeeId);
}
