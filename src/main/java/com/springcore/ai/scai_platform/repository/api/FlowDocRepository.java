package com.springcore.ai.scai_platform.repository.api;

import com.springcore.ai.scai_platform.entity.FlowDoc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlowDocRepository extends JpaRepository<FlowDoc, Long> {

}
