package com.ratnesh.buildly.entity;

import com.ratnesh.buildly.enums.MessageRole;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name ="chat_messages")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumns({
            @JoinColumn(name = "project_id", referencedColumnName = "project_id", nullable = false),
            @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    })
    ChatSession chatSession;


    @OneToMany(mappedBy = "chatMessage",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @OrderBy("sequenceOrder ASC")
    List<ChatEvent> events;


    @Column(columnDefinition = "text")
//  set column definition to "text" else hibernate generates varchar (255)
    String content; //NULL unless USER ROLE

    @Enumerated(value = EnumType.STRING)
    MessageRole role;

    Integer tokensUsed = 0;

    @CreationTimestamp
    Instant createdAt;


}
