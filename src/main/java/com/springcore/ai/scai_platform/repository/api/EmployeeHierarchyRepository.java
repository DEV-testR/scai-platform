package com.springcore.ai.scai_platform.repository.api;

import com.springcore.ai.scai_platform.entity.EmployeeHierarchy;
import com.springcore.ai.scai_platform.entity.EmployeeHierarchyId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeHierarchyRepository extends JpaRepository<EmployeeHierarchy, EmployeeHierarchyId> {

    boolean existsByIdAncestoridAndIdDescendantid(Long ancestorid, Long descendantid);

    /**
     * ลบความสัมพันธ์ระหว่าง "กิ่งพนักงานที่ถูกย้าย" ออกจาก "สายหัวหน้าเดิม"
     * (ลบทุก ancestor ของ employeeId ที่ไม่ใช่ตัวมันเอง ออกจาก descendant ทุกตัวในกิ่งของ employeeId)
     */
    @Modifying
    @Query(value = "DELETE FROM am_employee_hierarchy " +
            "WHERE descendantid IN (SELECT descendantid FROM am_employee_hierarchy WHERE ancestorid = :employeeId) " +
            "AND ancestorid IN (SELECT ancestorid FROM am_employee_hierarchy WHERE descendantid = :employeeId AND ancestorid != descendantid)",
            nativeQuery = true)
    void deleteOldHierarchy(@Param("employeeId") Long employeeId);

    /**
     * สร้างความสัมพันธ์ใหม่ระหว่าง "กิ่งพนักงานที่ถูกย้าย" เข้ากับ "สายหัวหน้าใหม่"
     * (Cross Join ระหว่าง ancestors ของหัวหน้าใหม่ และ descendants ของพนักงานที่ย้าย)
     */
    @Modifying
    @Query(value = "INSERT INTO am_employee_hierarchy (ancestorid, descendantid, depth) " +
            "SELECT super.ancestorid, sub.descendantid, super.depth + sub.depth + 1 " +
            "FROM am_employee_hierarchy super, am_employee_hierarchy sub " +
            "WHERE super.descendantid = :newManagerId AND sub.ancestorid = :employeeId",
            nativeQuery = true)
    void insertNewHierarchy(@Param("employeeId") Long employeeId, @Param("newManagerId") Long newManagerId);

    @Query(value = "SELECT e.id FROM am_employee e " +
            "JOIN am_employee_hierarchy h ON e.id = h.ancestorid " +
            "WHERE h.descendantid = :employeeId " +
            "ORDER BY h.depth DESC", nativeQuery = true)
    List<Long> getAncestorIds(@Param("employeeId") Long employeeId);
}