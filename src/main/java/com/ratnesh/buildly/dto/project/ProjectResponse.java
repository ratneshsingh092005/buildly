package com.ratnesh.buildly.dto.project;

import com.ratnesh.buildly.dto.auth.UserProfileResponse;

import java.time.Instant;

public record ProjectResponse(
    Long id,
    String name,
    Instant createdAt,
    Instant updatedAt,
    UserProfileResponse user
){
}
