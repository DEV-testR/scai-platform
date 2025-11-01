package com.springcore.ai.scai_platform.service.api;

import com.springcore.ai.scai_platform.entity.Instruction;
import com.springcore.ai.scai_platform.repository.InstructionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface InstructionService {

    List<Instruction> fetchAllInstructions();

    Instruction fetchInstructionById(Long id);

    Instruction createInstruction(Instruction instruction);

    Instruction updateInstruction(Long id, Instruction instructionDetails);

    boolean deleteInstruction(Long id);
}
