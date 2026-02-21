package com.springcore.ai.scai_platform.repository.api;

import com.springcore.ai.scai_platform.entity.FlowDoc;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FlowDocRepository extends JpaRepository<FlowDoc, Long> {

    // ค้นหา FlowDoc โดยใช้ docId
    Optional<FlowDoc> findByDocId(Long docId);

    @Transactional
    void deleteByDocId(Long docId);

    boolean existsByDocId(Long docId);

}
