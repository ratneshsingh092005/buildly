package com.ratnesh.buildly.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class ProjectMemberId {
    Long projectId;
    Long userId;
}
