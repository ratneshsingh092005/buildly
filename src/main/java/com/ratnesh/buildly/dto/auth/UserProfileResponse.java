package com.ratnesh.buildly.dto.auth;

public record UserProfileResponse(
        Long id,
        String username,
        String name
) {
}
