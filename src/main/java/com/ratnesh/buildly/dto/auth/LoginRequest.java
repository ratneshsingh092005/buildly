package com.ratnesh.buildly.dto.auth;

import jakarta.validation.constraints.*;

public record LoginRequest(
     @Email @NotBlank
     String username,
     String password
) {
}
