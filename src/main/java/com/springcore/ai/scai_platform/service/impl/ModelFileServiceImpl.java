package com.springcore.ai.scai_platform.service.impl;

import com.springcore.ai.scai_platform.entity.Instruction;
import com.springcore.ai.scai_platform.properties.ModelFileProperties;
import com.springcore.ai.scai_platform.repository.api.InstructionRepository;
import com.springcore.ai.scai_platform.service.api.ModelFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;


@Slf4j
@Service
public class ModelFileServiceImpl implements ModelFileService {

    private final InstructionRepository instructionRepository;
    private final ModelFileProperties fineTuneProperties;
    private final ChatModel chatModel;
    public ModelFileServiceImpl(InstructionRepository instructionRepository,
                                ModelFileProperties fineTuneProperties, ChatModel chatModel) {
        this.instructionRepository = instructionRepository;
        this.fineTuneProperties = fineTuneProperties;
        this.chatModel = chatModel;
    }

    @Override
    public String buildModelFile() {
        try {
            String systemPrompt = "You are a helpful assistant."; // ตัวอย่าง system prompt

            List<Instruction> instructions = instructionRepository.findAll();
            String baseModel = chatModel.getDefaultOptions().getModel();

            String modelName = createModelfile(baseModel, systemPrompt, instructions);
            return String.format("Modelfile %s created successfully for model '%s'. Please run 'ollama create %s -f %s' to finalize.",
                    baseModel,
                    modelName, modelName, fineTuneProperties.getModelfilepath());
        } catch (Exception e) {
            log.error("Fine-tuning pipeline failed.", e);
            return "Error during fine-tuning: " + e.getMessage();
        }
    }



    // --- Step 1: สร้าง Modelfile (.txt) (เหมือนเดิม) ---
    private String createModelfile(String baseModel, String systemPrompt, List<Instruction> fewShotInstructions) throws IOException {
        String modelName = fineTuneProperties.getModelname();
        String modelfilePath = fineTuneProperties.getModelfilepath();
        log.info("Creating Modelfile for custom model '{}' at: {}", modelName, modelfilePath);
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(modelfilePath))) {

            // 1. ส่วน FROM
            writer.write(String.format("FROM %s", baseModel));
            writer.newLine();
            writer.newLine();

            // 2. ส่วน SYSTEM
            writer.write("SYSTEM \"\"\"");
            writer.newLine();
            writer.write(systemPrompt); // 👈 ใช้ systemPrompt ที่ดึงจาก DB
            writer.newLine();
            writer.write("\"\"\"");
            writer.newLine();
            writer.newLine();

            // 3. ส่วน PARAMETERS
            writer.write("PARAMETER temperature 0.3");
            writer.newLine();
            writer.write("PARAMETER top_p 0.5");
            writer.newLine();
            writer.write("PARAMETER top_k 20");
            writer.newLine();
            writer.write("PARAMETER num_predict 100");
            writer.newLine();
            writer.write("PARAMETER num_ctx 4069");
            writer.newLine();

            writer.newLine();

            // 4. ส่วน MESSAGE (Few-Shot Examples)
            writer.write("# Few-Shot Examples (Instructions from Database)");
            writer.newLine();
            for (Instruction instruction : fewShotInstructions) {
                writer.write(String.format("MESSAGE user %s", escapeModelfile(instruction.getInstruction())));
                writer.newLine();
                writer.write(String.format("MESSAGE assistant %s", escapeModelfile(instruction.getOutput())));
                writer.newLine();
                writer.newLine();
            }
        }
        log.info("Modelfile for '{}' created successfully.", modelName);
        return modelName;
    }

    // ... escapeModelfile() ...
    private String escapeModelfile(String input) {
        if (input == null) return "";
        return input.replace("\"", "\\\"");
    }
}
