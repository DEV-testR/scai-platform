package com.springcore.ai.scai_platform.controller;

import com.springcore.ai.scai_platform.service.api.ModelFileService;
import com.springcore.ai.scai_platform.service.api.OllamaService;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v0/ollama")
public class OllamaController {

    private final ModelFileService modelFileService;
private final OllamaService ollamaService;

    public OllamaController(ModelFileService fineTuneService, OllamaService ollamaService) {
        this.modelFileService = fineTuneService;
        this.ollamaService = ollamaService;
    }

    @GetMapping("/chat")
    public ResponseEntity<String> chat(@RequestParam(required = false) String model,
                                       @RequestParam(value = "prompt", defaultValue = "What is the policy for annual leave?") String prompt) {
        try {
            log.info("Received chat prompt: {}", prompt);
            String output = ollamaService.chat(model, prompt);
            if (StringUtils.isEmpty(output)) {
                log.warn("Ollama returned an empty response.");
                return ResponseEntity.status(500).body("Error: Ollama returned an empty response.");
            }

            log.info("Ollama responded successfully.");
            return ResponseEntity.ok(output);
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
    public ResponseEntity<String> fineTuneModel() {
        String modelName = modelFileService.buildModelFile();
        log.info("Request to FineTune Ollama model: {}", modelName);
        return ResponseEntity.accepted().body(modelName);
    }
}
