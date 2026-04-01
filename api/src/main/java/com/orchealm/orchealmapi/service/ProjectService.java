package com.orchealm.orchealmapi.service;

import com.orchealm.orchealmapi.model.project.ProjectRecord;
import com.orchealm.orchealmapi.model.user.UserPrincipal;
import com.orchealm.orchealmapi.persistence.model.Project;
import com.orchealm.orchealmapi.persistence.model.ProjectView;
import com.orchealm.orchealmapi.persistence.model.ProjectViewQueryFilterDto;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Service interface for managing projects.
 * Provides operations to create, read, update, and delete projects, while enforcing
 * RLS (Row-Level Security) permissions based on the provided {@link UserPrincipal}.
 */
public interface ProjectService {
    /**
     * Creates a new project.
     *
     * @param userPrincipal the authenticated user performing the operation
     * @param projectRecord the data for the project to create
     * @return the newly created {@link Project} entity
     */
    Project createProject(UserPrincipal userPrincipal, ProjectRecord projectRecord);

    /**
     * Retrieves a paginated list of projects, optionally filtered by specific fields.
     *
     * @param userPrincipal the authenticated user performing the operation
     * @param filters       a map of field names to list of values to filter projects (e.g., key, name)
     * @param pageable      pagination information
     * @return a paginated list of {@link Project} entities matching the filters and accessible by the user
     */
    Page<ProjectView> findProjectsView(UserPrincipal userPrincipal,
                                       ProjectViewQueryFilterDto filters,
                                       Pageable pageable);

    /**
     * Retrieves a single project by its unique identifier.
     *
     * @param userPrincipal the authenticated user performing the operation
     * @param id            the UUID of the project to retrieve
     * @return the {@link Project} entity
     */
    Project findProjectById(UserPrincipal userPrincipal, UUID id);

    /**
     * Retrieves a single project by its unique identifier.
     *
     * @param userPrincipal the authenticated user performing the operation
     * @param id            the UUID of the project to retrieve
     * @return the {@link Project} entity
     */
    ProjectView findProjectViewById(UserPrincipal userPrincipal, UUID id);

    /**
     * Updates an existing project with new data.
     *
     * @param userPrincipal the authenticated user performing the operation
     * @param id            the UUID of the project to update
     * @param projectRecord the new data for the project
     * @return the updated {@link Project} entity
     */
    Project updateProject(UserPrincipal userPrincipal, UUID id, ProjectRecord projectRecord);

    /**
     * Deletes a project by its unique identifier.
     *
     * @param userPrincipal the authenticated user performing the operation
     * @param id            the UUID of the project to delete
     */
    void deleteProject(UserPrincipal userPrincipal, UUID id);

    Project findProjectByKey(UserPrincipal userPrincipal, @NotBlank String organization, @NotBlank String name);
}
