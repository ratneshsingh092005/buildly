package com.ratnesh.buildly.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

import static com.ratnesh.buildly.enums.ProjectPermission.*;

@RequiredArgsConstructor
@Getter
public enum ProjectRole {
    OWNER (Set.of(VIEW,EDIT,DELETE,MANAGE_MEMBERS,VIEW_MEMBERS)),
    EDITOR(Set.of(VIEW,EDIT,DELETE,VIEW_MEMBERS)),
    VIEWER(Set.of(VIEW,VIEW_MEMBERS));

    private final Set<ProjectPermission> permissions;


}
