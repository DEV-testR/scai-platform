package com.springcore.ai.scai_platform.repository.api;

import com.springcore.ai.scai_platform.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // ดึง 20 รายการล่าสุดของผู้ใช้งานคนนั้นๆ
    List<Notification> findTop20ByUserIdOrderByCreateDateDesc(Long userId);

    // นับจำนวนแจ้งเตือนที่ยังไม่ได้อ่าน
    long countByUserIdAndIsReadFalse(Long userId);
}
