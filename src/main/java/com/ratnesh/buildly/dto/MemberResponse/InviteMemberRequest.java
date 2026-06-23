package com.ratnesh.buildly.dto.MemberResponse;

import com.ratnesh.buildly.enums.ProjectRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record InviteMemberRequest(

    @Email @NotBlank
    String username,
    //only notnull sufficient no need of @Enumerated
    @NotNull
    ProjectRole role
) {
}
