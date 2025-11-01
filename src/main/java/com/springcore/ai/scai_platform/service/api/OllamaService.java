package com.springcore.ai.scai_platform.service.api;

import reactor.core.publisher.Flux;

public interface OllamaService {
    String chat(String model, String prompt);
    Flux<String> chatStream(String model, String prompt);
}
