package com.ratnesh.buildly.repository;

import com.ratnesh.buildly.entity.Project;
import com.ratnesh.buildly.enums.ProjectRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project,Long> {

    @Query("""
          SELECT p as project, pm.projectRole as role
                      FROM Project p
                      JOIN ProjectMember pm ON pm.project.id = p.id
                      WHERE pm.user.id = :userId
                        AND p.deletedAt IS NULL
                      ORDER BY p.updatedAt DESC  
""")
    List<ProjectWithRole> findAllProjectAccessibleByUser(@Param("userId") Long userId);

    @Query("""
           SELECT p FROM Project p
           WHERE p.id =:projectId
           AND p.deletedAt IS NULL
           AND EXISTS(
            SELECT 1 FROM ProjectMember pm
            WHERE pm.id.userId =:userId
            AND pm.id.projectId = p.id
            )
            ORDER BY p.updatedAt DESC
""")
    //projectId unique so only one project hence optional
    Optional<Project> findAccessibleProjectById(@Param("projectId") Long ProjectId , @Param("userId") Long userId);

    @Query("""
          SELECT p as project, pm.projectRole as role
                      FROM Project p
                      JOIN ProjectMember pm ON pm.project.id = p.id
                      WHERE p.id = :projectId
                        AND pm.user.id = :userId
                        AND p.deletedAt IS NULL
""")
    Optional<ProjectWithRole> findAccessibleProjectByIdWithRole(@Param("projectId") Long ProjectId , @Param("userId") Long userId);


    interface ProjectWithRole{
        Project getProject();
        ProjectRole getRole();
    }
}
