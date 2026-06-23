package com.ratnesh.buildly.service.Impl;

import com.ratnesh.buildly.dto.chat.ChatResponse;
import com.ratnesh.buildly.entity.ChatMessage;
import com.ratnesh.buildly.entity.ChatSession;
import com.ratnesh.buildly.entity.ChatSessionId;
import com.ratnesh.buildly.mapper.ChatMapper;
import com.ratnesh.buildly.repository.ChatMessageRepository;
import com.ratnesh.buildly.repository.ChatSessionRepository;
import com.ratnesh.buildly.security.AuthUtil;
import com.ratnesh.buildly.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j

public class ChatServiceImpl implements ChatService {
    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final AuthUtil authUtil;
    private final ChatMapper chatMapper;

    @Override
    public List<ChatResponse> getProjectChatHistory(Long projectId) {
        Long userId = authUtil.getCurrentUserId();
        ChatSession chatSession = chatSessionRepository.getReferenceById(new ChatSessionId(projectId,userId));

        List<ChatMessage> chatMessageList = chatMessageRepository.findByChatSession(chatSession);
        return chatMapper.toChatResponse(chatMessageList);

    }
}
