package com.springcore.ai.scai_platform.service;

import com.springcore.ai.scai_platform.controller.InstructionController;
import com.springcore.ai.scai_platform.entity.Instruction;
import com.springcore.ai.scai_platform.repository.InstructionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;

@Service
public class InstructionService {
    private static final Logger log = LoggerFactory.getLogger(InstructionService.class);
    private final InstructionRepository instructionRepository;

    @Autowired
    public InstructionService(InstructionRepository instructionRepository) {
        this.instructionRepository = instructionRepository;
    }

    public List<Instruction> fetchAllInstructions() {
        log.info("Fetching all instructions from the dataset.");
        return instructionRepository.findAll();
    }

    public Instruction fetchInstructionById(@PathVariable Long id) {
        return instructionRepository.findById(id).orElse(null);
    }

    public Instruction createInstruction(Instruction instruction) {
        // ID จะถูกสร้างโดย DB อัตโนมัติ
        Instruction savedInstruction = instructionRepository.save(instruction);
        log.info("New instruction created with ID: {}", savedInstruction.getId());
        return savedInstruction;
    }

    public Instruction updateInstruction(Long id, @RequestBody Instruction instructionDetails) {
        Instruction existingInstruction = fetchInstructionById(id);
        if (existingInstruction != null) {
            // อัปเดตเฉพาะ field ที่อนุญาต
            existingInstruction.setInstruction(instructionDetails.getInstruction());
            existingInstruction.setOutput(instructionDetails.getOutput());
            Instruction updatedInstruction = instructionRepository.save(existingInstruction);
            log.info("Instruction updated for ID: {}", id);
            return updatedInstruction;
        } else {
            log.warn("Cannot update. Instruction not found for ID: {}", id);
            return null;
        }
    }

    public boolean deleteInstruction(@PathVariable Long id) {
        if (instructionRepository.existsById(id)) {
            instructionRepository.deleteById(id);
            log.info("Instruction deleted for ID: {}", id);
            return true;
        } else {
            log.warn("Cannot delete. Instruction not found for ID: {}", id);
            return false;
        }
    }

}
