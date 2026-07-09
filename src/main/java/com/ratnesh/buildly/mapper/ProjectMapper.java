package com.ratnesh.buildly.mapper;

import com.ratnesh.buildly.dto.project.ProjectResponse;
import com.ratnesh.buildly.dto.project.ProjectSummaryResponse;
import com.ratnesh.buildly.entity.Project;
import com.ratnesh.buildly.enums.ProjectRole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    ProjectResponse toProjectResponse(Project project);
//    @Mapping(target = "role", source = "projectRole")
    ProjectSummaryResponse toProjectSummaryResponse(Project project, ProjectRole role);

    List<ProjectSummaryResponse> toListOfProjectSummaryResponse(List<Project> projects );

}
