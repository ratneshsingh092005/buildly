package com.ratnesh.buildly.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
//@Entity

public class UsageLog {

    Long id;
//    User user;
//    Project project;
    Integer tokenUsed;
    Integer durationMs;
    String metadata;
    Instant createdAt;
}
