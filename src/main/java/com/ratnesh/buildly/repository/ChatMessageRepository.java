package com.ratnesh.buildly.repository;

import com.ratnesh.buildly.entity.ChatMessage;
import com.ratnesh.buildly.entity.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage,Long> {

    //to resolve N+1 query problem
    @Query("""
            SELECT DISTINCT m FROM ChatMessage m
            LEFT JOIN FETCH m.events e
            WHERE m.chatSession =:chatSession
            ORDER BY m.createdAt ASC,e.sequenceOrder ASC
""")
    List<ChatMessage> findByChatSession(ChatSession chatSession);
}
