package com.ratnesh.buildly.controller;

import com.ratnesh.buildly.dto.chat.ChatRequest;
import com.ratnesh.buildly.dto.chat.ChatResponse;
import com.ratnesh.buildly.service.AiGenerationService;
import com.ratnesh.buildly.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatController {
    private final AiGenerationService aiGenerationService;
    private final ChatService chatService;
    @PostMapping(value = "/stream",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> streamChat(
            @RequestBody ChatRequest request
    ){
        return aiGenerationService.streamResponse(request.message(),request.projectId())
//                we get Flux<String> but we want Flux<ServerSentEvent<String>> so map it
                .map(data -> ServerSentEvent.<String>builder()
                        .data(data)
                        .build()
                );
    }

    @GetMapping("/projects/{projectId}")
    public ResponseEntity<List<ChatResponse>> getChatHistory(
            @PathVariable Long projectId
    ){
        return ResponseEntity.ok(chatService.getProjectChatHistory(projectId));
    }
}
