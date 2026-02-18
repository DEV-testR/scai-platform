package com.springcore.ai.scai_platform.repository.api;

import com.springcore.ai.scai_platform.entity.Document;
import com.springcore.ai.scai_platform.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long>, JpaSpecificationExecutor<Document> {

    Optional<Document> findByDocumentNo(String documentNo);

    List<Document> findByDocumentType(String documentType);

    List<Document> findByEmId(Employee emId);

    @Query("""
    select d from Document d
    where d.documentType = :documentType
    and function('to_char', d.dateWork, 'YYYYMM') = :dateFormat
    order by d.id desc
    """)
    List<Document> findTopByTypeAndMonthOrderByIdDesc(
            String documentType,
            String dateFormat
    );

}
