package com.ratnesh.buildly.dto.MemberResponse;

import com.ratnesh.buildly.enums.ProjectRole;
import jakarta.validation.constraints.NotNull;

public record UpdateMemberRoleRequest(
        @NotNull
        ProjectRole role
) {
}
