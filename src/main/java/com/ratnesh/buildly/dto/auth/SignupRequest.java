package com.ratnesh.buildly.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignupRequest(
        @Email @NotBlank
        String username,
        @NotBlank
        @Size(min  = 1,max = 50)
        String name,
        @NotBlank
        @Size(min = 4,max = 50)   //@Min doesn't work with strings
        String password
) {
}
