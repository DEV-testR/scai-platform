package com.springcore.ai.scai_platform.repository.api;

import com.springcore.ai.scai_platform.entity.Trafts;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface TraftsRepository extends JpaRepository<Trafts, Long> {
    Optional<Trafts> findByEmployeeIdAndIscurrentTrue(Long employeeId);

    @Query(value = "SELECT * FROM am_trafts WHERE employeeid = :emId " +
            "ORDER BY effectivedate DESC, id DESC LIMIT 1",
            nativeQuery = true)
    Optional<Trafts> findLatestTraftByEmployeeId(@Param("emId") Long emId);

    @Query(value = """
    SELECT * FROM (
        SELECT *, ROW_NUMBER() OVER (PARTITION BY employeeid ORDER BY effectivedate DESC, id DESC) as rn
        FROM am_trafts
        WHERE employeeid IN :employeeIds
    ) as ranked_trafts
    WHERE rn = 1
    """, nativeQuery = true)
    List<Trafts> findLatestTraftsByEmployeeIds(@Param("employeeIds") Set<Long> employeeIds);
    /**
     * ดึงข้อมูล Traft ปัจจุบันของพนักงานทุกคนเพื่อไปวาด Org Chart
     * @EntityGraph จะสั่งให้ Hibernate ทำ LEFT JOIN กับตารางที่ระบุทันที
     * ทำให้ไม่ต้องวนลูป Query ทีละตัว (แก้ปัญหา N+1)
     */
    @EntityGraph(attributePaths = {"employee", "manager", "department", "position", "jobRoles"})
    List<Trafts> findAllByIscurrentTrue();

    /**
     * สำหรับหาพนักงานระดับสูงสุด (CEO/Root) ที่ไม่มีหัวหน้า
     */
    @EntityGraph(attributePaths = {"employee", "position", "department", "jobRoles"})
    List<Trafts> findAllByManagerIsNullAndIscurrentTrue();

    /**
     * สำหรับหาลูกน้องสายตรงของหัวหน้าคนนั้นๆ (ใช้สำหรับ Lazy Loading)
     */
    @EntityGraph(attributePaths = {"employee", "position", "department", "jobRoles"})
    List<Trafts> findAllByManagerIdAndIscurrentTrue(Long managerId);

    // ค้นหาว่ามีลูกน้องที่ยัง Active อยู่ใต้ Manager คนนี้หรือไม่
    boolean existsByManagerIdAndIscurrentTrue(Long managerId);
}