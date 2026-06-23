package com.ratnesh.buildly.dto.auth;

public record AuthResponse(
        String token,
        UserProfileResponse user
        ) {
}
