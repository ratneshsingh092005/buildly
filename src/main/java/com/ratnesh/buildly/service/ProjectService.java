package com.ratnesh.buildly.service;

import com.ratnesh.buildly.dto.project.ProjectRequest;
import com.ratnesh.buildly.dto.project.ProjectResponse;
import com.ratnesh.buildly.dto.project.ProjectSummaryResponse;

import java.util.List;

public interface ProjectService {
    List<ProjectSummaryResponse> getUserProjects();

    ProjectSummaryResponse getUserProjectById( Long id);

    ProjectResponse createProject(ProjectRequest request);

    ProjectResponse updateProduct(Long id, ProjectRequest request);

    void softDelete(Long id);
}
