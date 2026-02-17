package com.springcore.ai.scai_platform.repository.api;

import com.springcore.ai.scai_platform.entity.Instruction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstructionRepository extends JpaRepository<Instruction, Long> {
}
