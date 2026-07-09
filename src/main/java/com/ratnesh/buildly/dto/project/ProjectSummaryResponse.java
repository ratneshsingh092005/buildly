package com.ratnesh.buildly.dto.project;

import com.ratnesh.buildly.enums.ProjectRole;

import java.time.Instant;

public record ProjectSummaryResponse(
        Long id,
        String name,
        Instant createdAt,
        Instant updatedAt,
        ProjectRole role
) {
}
