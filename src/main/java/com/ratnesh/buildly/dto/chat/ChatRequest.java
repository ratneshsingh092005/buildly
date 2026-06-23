package com.ratnesh.buildly.dto.chat;

public record ChatRequest(
        String message,
        Long projectId
) {
}
