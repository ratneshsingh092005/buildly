package com.ratnesh.buildly.service.Impl;

import com.ratnesh.buildly.dto.project.ProjectRequest;
import com.ratnesh.buildly.dto.project.ProjectResponse;
import com.ratnesh.buildly.dto.project.ProjectSummaryResponse;
import com.ratnesh.buildly.entity.Project;
import com.ratnesh.buildly.entity.ProjectMember;
import com.ratnesh.buildly.entity.ProjectMemberId;
import com.ratnesh.buildly.entity.User;
import com.ratnesh.buildly.enums.ProjectRole;
import com.ratnesh.buildly.error.BadRequestException;
import com.ratnesh.buildly.error.ResourceNotFoundException;
import com.ratnesh.buildly.mapper.ProjectMapper;
import com.ratnesh.buildly.repository.ProjectMemberRepository;
import com.ratnesh.buildly.repository.ProjectRepository;
import com.ratnesh.buildly.repository.UserRepository;
import com.ratnesh.buildly.security.AuthUtil;
import com.ratnesh.buildly.service.ProjectService;
import com.ratnesh.buildly.service.ProjectTemplateService;
import com.ratnesh.buildly.service.SubscriptionService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
@Transactional

public class ProjectServiceImpl implements ProjectService {

    AuthUtil authUtil;
    ProjectMapper projectMapper;
    ProjectRepository projectRepository;
    ProjectMemberRepository projectMemberRepository;
    UserRepository userRepository;
    SubscriptionService subscriptionService;
    ProjectTemplateService projectTemplateService;

    @Override
    public List<ProjectSummaryResponse> getUserProjects() {

//        return projectRepository.findAllProjectAccessibleByUser(userId)
//                .stream()
//                .map(projectMapper::toProjectSummaryResponse)
//                .collect(Collectors.toList());
        Long userId = authUtil.getCurrentUserId();

        return projectMapper.toListOfProjectSummaryResponse(projectRepository.findAllProjectAccessibleByUser(userId));
    }

    @Override

    @PreAuthorize("@security.canViewProject(#projectId)")
    public ProjectResponse getUserProjectById( Long projectId) {
        Long userId = authUtil.getCurrentUserId();
        Project project = getAccessibleProjectById(projectId,userId);
        return projectMapper.toProjectResponse(project);
    }

    @Override
    public ProjectResponse createProject(ProjectRequest request) {

        if(!subscriptionService.canCreateNewProject()){
            throw new BadRequestException("User cannot create a new project with current plan, Upgrade plan now");
        }
        Long userId = authUtil.getCurrentUserId();
//        User owner = userRepository.findById(userId).orElseThrow(()-> new ResourceNotFoundException("User",userId.toString()));

        //this won't make database call
        //it creates a hibernate proxy object

       User owner = userRepository.getReferenceById(userId);



        Project project = Project.builder()
                .name(request.name())
                .isPublic(false)
                .build();
        project = projectRepository.save(project);


        ProjectMemberId projectMemberId = new ProjectMemberId(project.getId(), owner.getId());
        ProjectMember projectMember = ProjectMember.builder()
                .id(projectMemberId)
                .projectRole(ProjectRole.OWNER)
                .user(owner)
                .acceptedAt(Instant.now())
                .invitedAt(Instant.now())
                .project(project)
                .build();

        projectMemberRepository.save(projectMember);

        projectTemplateService.initializeProjectFromTemplate(project.getId());
        return projectMapper.toProjectResponse(project);
    }



    @Override
    @PreAuthorize("@security.canEditProject(#projectId)")
    public ProjectResponse updateProduct(Long projectId, ProjectRequest request) {
        Long userId = authUtil.getCurrentUserId();
        Project project = getAccessibleProjectById(projectId,userId);
        project.setName(request.name());
        project = projectRepository.save(project);
        return projectMapper.toProjectResponse(project);
    }

    @Override
    @PreAuthorize("@security.canDeleteProject(#projectId)")
    public void softDelete(Long projectId) {
        Long userId = authUtil.getCurrentUserId();
        Project project = getAccessibleProjectById(projectId,userId);

        project.setDeletedAt(Instant.now());
       projectRepository.save(project);
//no need of .save() though bz of dirty checking due to use of transactional
    }



    public Project getAccessibleProjectById(Long projectId,Long userId){
        return projectRepository.findAccessibleProjectById(projectId,userId)
                .orElseThrow(()->new ResourceNotFoundException("Project",projectId.toString()));
    }
}
