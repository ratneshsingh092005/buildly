package com.ratnesh.buildly.service;

import reactor.core.publisher.Flux;

public interface AiGenerationService {
    Flux<String> streamResponse(String userMessage, Long projectId);
}
