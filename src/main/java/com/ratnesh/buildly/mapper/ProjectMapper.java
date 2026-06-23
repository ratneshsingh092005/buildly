package com.ratnesh.buildly.mapper;

import com.ratnesh.buildly.dto.project.ProjectResponse;
import com.ratnesh.buildly.dto.project.ProjectSummaryResponse;
import com.ratnesh.buildly.entity.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    ProjectResponse toProjectResponse(Project project);

    ProjectSummaryResponse toProjectSummaryResponse(Project project);

    List<ProjectSummaryResponse> toListOfProjectSummaryResponse(List<Project> projects );

}
