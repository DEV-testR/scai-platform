package com.springcore.ai.scai_platform.repository.api;

import com.springcore.ai.scai_platform.dto.DocumentSearchReq;
import com.springcore.ai.scai_platform.entity.Document;
import com.springcore.ai.scai_platform.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepositoryCustom {
    List<Document> searchByCriteria(DocumentSearchReq criteria);
}
