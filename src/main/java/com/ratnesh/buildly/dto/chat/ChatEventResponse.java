package com.ratnesh.buildly.dto.chat;

import com.ratnesh.buildly.enums.ChatEventType;

public record ChatEventResponse(
        Long id,
        ChatEventType type,
        Integer sequenceOrder,
        String content,
        String filePath,
        String metadata
) {
}
