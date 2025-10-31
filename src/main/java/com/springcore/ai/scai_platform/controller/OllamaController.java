package com.springcore.ai.scai_platform.controller;

import com.springcore.ai.scai_platform.service.FineTuneService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v0/ollama")
public class OllamaController {

    private static final Logger log = LoggerFactory.getLogger(OllamaController.class);
    private final FineTuneService fineTuneService;

    // Spring AI ChatClient ที่กำหนดค่าด้วย Ollama ใน YAML
    private final ChatClient chatClient;

    @Autowired
    public OllamaController(ChatClient.Builder chatClientBuilder, FineTuneService fineTuneService) {
        // สร้าง ChatClient โดยใช้ Builder เพื่อดึง Configuration จาก YAML
        this.chatClient = chatClientBuilder.build();
        this.fineTuneService = fineTuneService;
    }

    /**
     * Endpoint สำหรับเรียกใช้ LLM (Inference)
     * URI: GET /api/v1/ollama/chat?prompt=...
     */
    @GetMapping("/chat")
    public ResponseEntity<String> chat(@RequestParam(value = "prompt", defaultValue = "What is the policy for annual leave?") String prompt) {
        try {
            log.info("Received chat prompt: {}", prompt);

            // 1. สร้าง Prompt และเรียกใช้ ChatClient
            // 2. ใช้ .content() เพื่อดึง String Output ออกมาโดยตรง (เป็น synchronous call)
            String output = chatClient.prompt()
                    .user(prompt) // กำหนด User Prompt โดยตรง
                    .call()
                    .content(); // <--- แก้ไข: ใช้ .content() แทน .block()

            if (output != null && !output.isEmpty()) {
                log.info("Ollama responded successfully.");
                return ResponseEntity.ok(output);
            } else {
                log.warn("Ollama returned an empty response.");
                return ResponseEntity.status(500).body("Error: Ollama returned an empty response.");
            }

        } catch (Exception e) {
            log.error("Error during Ollama chat inference.", e);
            return ResponseEntity.internalServerError().body("An error occurred: " + e.getMessage());
        }
    }

    // ... (ส่วนของ pullModel ยังคงเดิม) ...
    @PostMapping("/pull-model")
    public ResponseEntity<String> pullModel(@RequestParam(value = "name") String modelName) {
        log.info("Request to pull Ollama model: {}", modelName);
        return ResponseEntity.accepted().body("Model pull for " + modelName + " initiated. Check Ollama logs.");
    }

    @PostMapping("/fine-tune")
    public ResponseEntity<String> fineTuneModel(@RequestParam(value = "name") String modelName) {
        String messageTune = fineTuneService.runFineTune();
        log.info("Request to FineTune Ollama model: {} message: {}", modelName, messageTune);
        return ResponseEntity.accepted().body("Model pull for " + modelName + " initiated. Check Ollama logs.");
    }
}
