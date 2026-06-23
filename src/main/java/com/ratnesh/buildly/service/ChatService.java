package com.ratnesh.buildly.service;

import com.ratnesh.buildly.dto.chat.ChatResponse;

import java.util.List;

public interface ChatService {

    List<ChatResponse> getProjectChatHistory(Long projectId);
}
