package com.springcore.ai.scai_platform.service.api;

import com.springcore.ai.scai_platform.entity.Instruction;

import java.util.List;

public interface InstructionService {

    List<Instruction> fetchAllInstructions();

    Instruction fetchInstructionById(Long id);

    Instruction createInstruction(Instruction instruction);

    Instruction updateInstruction(Long id, Instruction instructionDetails);

    boolean deleteInstruction(Long id);
}
