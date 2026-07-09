package com.ratnesh.buildly.service;

import com.ratnesh.buildly.dto.chat.StreamResponse;
import reactor.core.publisher.Flux;

public interface AiGenerationService {
    Flux<StreamResponse> streamResponse(String userMessage, Long projectId);
}
