package com.springcore.ai.scai_platform.repository.api;

import com.springcore.ai.scai_platform.entity.Trafts;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TraftsRepository extends JpaRepository<Trafts, Long> {
    Optional<Trafts> findByEmployeeIdAndIscurrentTrue(Long employeeId);

    /**
     * ดึงข้อมูล Traft ปัจจุบันของพนักงานทุกคนเพื่อไปวาด Org Chart
     * @EntityGraph จะสั่งให้ Hibernate ทำ LEFT JOIN กับตารางที่ระบุทันที
     * ทำให้ไม่ต้องวนลูป Query ทีละตัว (แก้ปัญหา N+1)
     */
    @EntityGraph(attributePaths = {"employee", "manager", "department", "position", "jobRoles"})
    List<Trafts> findAllByIscurrentTrue();
}