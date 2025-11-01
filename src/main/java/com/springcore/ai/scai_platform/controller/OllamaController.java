package com.springcore.ai.scai_platform.controller;

import com.springcore.ai.scai_platform.dto.AIChatRequest;
import com.springcore.ai.scai_platform.service.api.ModelFileService;
import com.springcore.ai.scai_platform.service.api.OllamaService;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

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

    @PostMapping("/chat")
    public ResponseEntity<String> chat(@RequestBody AIChatRequest request) {
        try {
            String prompt = request.getPrompt();
            log.info("Received chat prompt: {}", prompt);
            String output = ollamaService.chat(request.getModel(), prompt);
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

    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE) // *** สำคัญมาก: กำหนดเป็น SSE ***
    public Flux<String> streamChat(@RequestBody AIChatRequest request) {
        return ollamaService.chatStream(request.getModel(), request.getPrompt());
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
