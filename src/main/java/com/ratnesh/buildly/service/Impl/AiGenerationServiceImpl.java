package com.ratnesh.buildly.service.Impl;

import com.ratnesh.buildly.dto.chat.StreamResponse;
import com.ratnesh.buildly.entity.*;
import com.ratnesh.buildly.enums.ChatEventType;
import com.ratnesh.buildly.enums.MessageRole;
import com.ratnesh.buildly.error.ResourceNotFoundException;
import com.ratnesh.buildly.llm.LlmResponseParser;
import com.ratnesh.buildly.llm.PromptUtils;
import com.ratnesh.buildly.llm.advisors.FileTreeContextAdvisor;
import com.ratnesh.buildly.llm.tools.CodeGenerationTools;
import com.ratnesh.buildly.repository.*;
import com.ratnesh.buildly.security.AuthUtil;
import com.ratnesh.buildly.service.AiGenerationService;
import com.ratnesh.buildly.service.ProjectFileService;
import com.ratnesh.buildly.service.UsageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiGenerationServiceImpl implements AiGenerationService {

    private final AuthUtil authUtil;
    private final ChatClient chatClient;
    private final ProjectFileService projectFileService;
    private static final Pattern FILE_TAG_PATTERN = Pattern.compile("<file path=\"([^\"]+)\">(.*?)</file>", Pattern.DOTALL);
    private final FileTreeContextAdvisor fileTreeContextAdvisor;
    private final LlmResponseParser llmResponseParser;
    private final ChatSessionRepository chatSessionRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatEventRepository chatEventRepository;
    private final UsageLogRepository usageLogRepository;
    private final UsageService usageService;
    @Override
    // AI might modify the project by user's prompt
    //only users with edit permission should be allowed to do so
    @PreAuthorize("@security.canEditProject(#projectId)")
    public Flux<StreamResponse> streamResponse(String userMessage, Long projectId) {
        usageService.checkDailyTokensUsage();
        Long userId = authUtil.getCurrentUserId();
        ChatSession chatSession = createChatSessionIfNotExists(projectId,userId);

        Map<String,Object> advisorParams = Map.of(
                "userId",userId,
                "projectId",projectId
        );
        StringBuilder fullResponseBuffer = new StringBuilder();
        CodeGenerationTools codeGenerationTools = new CodeGenerationTools(projectFileService,projectId);
        long startTime = System.currentTimeMillis();
//        // AtomicReference is used because endTime is updated inside lambda callbacks.
        AtomicReference<Long> endTime = new AtomicReference<>(0L);
        AtomicReference<Usage> usageRef = new AtomicReference<>();

        return chatClient.prompt()
                .system(PromptUtils.CODE_GENERATION_SYSTEM_PROMPT)
                .user(userMessage)
                .tools(codeGenerationTools)
                .advisors(
                        advisorSpec -> {
                            advisorSpec.params(advisorParams);
                            advisorSpec.advisors(fileTreeContextAdvisor);
                           }
                )
                .stream()
//// ChatResponse contains the response text plus metadata (tokens, model info, finish reason, etc.).
                .chatResponse()
                .doOnNext(response->{
                    String content = response.getResult().getOutput().getText();
                    if (content!=null && !content.isEmpty() && endTime.get()==0){
                        endTime.set(System.currentTimeMillis());
                    }
                    if(response.getMetadata().getUsage() != null) {
                        usageRef.set(response.getMetadata().getUsage());
                    }
                    fullResponseBuffer.append(content);
                })
                .doOnComplete(()->{
                    Schedulers.boundedElastic().schedule(()->{
                        Long duration = (endTime.get() - startTime)/1000;
                        finalizeChats(userMessage,chatSession,fullResponseBuffer.toString(),duration,usageRef.get());
                    });
                })
                .doOnError(error -> log.error("Error during streaming for projectId: {}", projectId))
                .map(chatResponse ->{
                        String text = Objects.requireNonNull(chatResponse.getResult().getOutput().getText());
                        return new StreamResponse(text!=null? text : "");
                });
//map bz currently we have Flux<ChatResponse> we want Flux<String>
    }


    private void finalizeChats(String userMessage, ChatSession chatSession, String fullText, Long duration,Usage usage) {
        Long projectId = chatSession.getProject().getId();

        if(usage != null) {
            int totalTokens = usage.getTotalTokens();
            usageService.recordTokenUsage(chatSession.getUser().getId(), totalTokens);
        }
        // Save the User message
        chatMessageRepository.save(
                ChatMessage.builder()
                        .chatSession(chatSession)
                        .role(MessageRole.USER)
                        .content(userMessage)
                        .tokensUsed(usage.getPromptTokens())
                        .build()
        );

        ChatMessage assistantChatMessage = ChatMessage.builder()
                .role(MessageRole.ASSISTANT)
                .content("Assistant Message here...")
                .chatSession(chatSession)
                .tokensUsed(usage.getCompletionTokens())
                .build();

        assistantChatMessage = chatMessageRepository.save(assistantChatMessage);

        List<ChatEvent> chatEventList = llmResponseParser.parseChatEvents(fullText, assistantChatMessage);
        chatEventList.add(0,
                ChatEvent.builder()
                        .type(ChatEventType.THOUGHT)
                        .chatMessage(assistantChatMessage)
                        .content("Thought for " + duration + "s")
                        .sequenceOrder(0)
                        .build()
        );

        chatEventList.stream()
                .filter(e -> e.getType() == ChatEventType.FILE_EDIT)
                .forEach(e -> projectFileService.saveFile(projectId, e.getFilePath(), e.getContent()));

        chatEventRepository.saveAll(chatEventList);
    }




    private ChatSession createChatSessionIfNotExists(Long projectId, Long userId) {
        ChatSessionId chatSessionId = new ChatSessionId(projectId, userId);
        ChatSession chatSession = chatSessionRepository.findById(chatSessionId).orElse(null);

        if(chatSession == null) {
            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> new ResourceNotFoundException("Project", projectId.toString()));
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", userId.toString()));

            chatSession = ChatSession.builder()
                    .id(chatSessionId)
                    .project(project)
                    .user(user)
                    .build();

            chatSession = chatSessionRepository.save(chatSession);
        }
        return chatSession;
    }
}


