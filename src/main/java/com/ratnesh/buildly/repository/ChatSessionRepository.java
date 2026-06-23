package com.ratnesh.buildly.repository;

import com.ratnesh.buildly.entity.ChatSession;
import com.ratnesh.buildly.entity.ChatSessionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface ChatSessionRepository extends JpaRepository<ChatSession, ChatSessionId> {
}
