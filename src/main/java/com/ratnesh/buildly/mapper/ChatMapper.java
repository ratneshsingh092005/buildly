package com.ratnesh.buildly.mapper;

import com.ratnesh.buildly.dto.chat.ChatResponse;
import com.ratnesh.buildly.entity.ChatMessage;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ChatMapper {
    List<ChatResponse> toChatResponse(List<ChatMessage> chatMessageList);
}
