package com.springcore.ai.scai_platform.repository.api;

import com.springcore.ai.scai_platform.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // หาพนักงานที่ไม่มี active trafts (ยังไม่ได้จัดตำแหน่ง)
    @Query("SELECT e FROM Employee e WHERE e.id NOT IN " +
            "(SELECT t.employee.id FROM Trafts t WHERE t.iscurrent = true)")
    List<Employee> findUnassignedEmployees();
}
