package com.ratnesh.buildly.dto.MemberResponse;

import com.ratnesh.buildly.enums.ProjectRole;

import java.time.Instant;

public record MemberResponse(
        Long userId,
        String username,
        String name,
        ProjectRole projectRole,
        Instant invitedAt
) {
}
