package com.orchealm.orchealmapi.controller;

import com.orchealm.orchealmapi.model.common.PageResponse;
import com.orchealm.orchealmapi.model.project.ProjectDTO;
import com.orchealm.orchealmapi.model.project.ProjectMapper;
import com.orchealm.orchealmapi.model.project.ProjectRecord;
import com.orchealm.orchealmapi.model.project.ProjectViewDTO;
import com.orchealm.orchealmapi.model.user.UserPrincipal;
import com.orchealm.orchealmapi.persistence.model.ProjectViewQueryFilterDto;
import com.orchealm.orchealmapi.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * REST controller exposing endpoints for managing Projects.
 *
 * <p>Supports CRUD operations with RLS enforcement via {@link UserPrincipal}.
 * Each endpoint logs the authenticated user and the performed action.
 */
@Slf4j
@RestController
@RequestMapping("/projects")
@Tag(name = "Project", description = "Endpoints to create, read, update, and delete projects")
public class ProjectController implements PagedResponseStatusResolver {

    /**
     * Service providing project management operations.
     */
    private final ProjectService service;

    /**
     * Mapper for converting between Project entities and {@link ProjectDTO}.
     */
    private final ProjectMapper mapper;

    /**
     * Constructs the ProjectsController with required dependencies.
     *
     * @param service the project service
     * @param mapper  the project mapper
     */
    @Autowired
    public ProjectController(final ProjectService service, final ProjectMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    /**
     * Creates a new project.
     *
     * @param projectRecord the project data to create
     * @param userPrincipal the authenticated user performing the action
     * @return the created {@link ProjectDTO} with HTTP status 201
     */
    @Operation(
        summary = "Create a project",
        description = "Creates a new project with the provided information."
    )
    @PostMapping()
    public ResponseEntity<ProjectDTO> create(final @RequestBody @Valid ProjectRecord projectRecord,
                                             final @AuthenticationPrincipal UserPrincipal userPrincipal) {
        log.info("[{}] Received POST request to create project with {}", userPrincipal.getEmail(), projectRecord);

        var project = service.createProject(userPrincipal, projectRecord);

        return ResponseEntity.status(HttpStatus.CREATED.value())
            .body(mapper.toDto(project));
    }

    /**
     * Retrieves a paginated list of projects with optional filters.
     *
     * @param filters       filter criteria as a map of field name to values
     * @param pageable      pagination and sorting information
     * @param userPrincipal the authenticated user performing the action
     * @return a paginated list of {@link ProjectDTO}
     */
    @Operation(
        summary = "Get projects",
        description = "Retrieves a paginated list of projects, optionally filtered by field values."
    )
    @GetMapping()
    public ResponseEntity<PageResponse<ProjectViewDTO>> find(
        final @ModelAttribute ProjectViewQueryFilterDto filters,
        final @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable,
        final @AuthenticationPrincipal UserPrincipal userPrincipal) {
        log.info("[{}] Received GET request to get projects with {}", userPrincipal.getEmail(), filters);

        Page<ProjectViewDTO> resources = service.findProjectsView(userPrincipal, filters, pageable)
            .map(mapper::toDto);

        return ResponseEntity.status(this.getStatus(resources)).body(PageResponse.of(resources, pageable));
    }

    /**
     * Retrieves a project by its ID.
     *
     * @param id            the UUID of the project
     * @param userPrincipal the authenticated user performing the action
     * @return the project as {@link ProjectDTO}
     */
    @Operation(
        summary = "Get project by ID",
        description = "Retrieves a specific project by its UUID."
    )
    @GetMapping("/{id}")
    public ResponseEntity<ProjectViewDTO> findById(final @PathVariable @NotNull UUID id,
                                                   final @AuthenticationPrincipal UserPrincipal userPrincipal) {
        log.info("[{}] Received GET request to get project with id {}", userPrincipal.getEmail(), id);
        var project = service.findProjectViewById(userPrincipal, id);

        return ResponseEntity.ok(mapper.toDto(project));
    }

    /**
     * Updates an existing project.
     *
     * @param id            the UUID of the project to update
     * @param projectRecord the updated project data
     * @param userPrincipal the authenticated user performing the action
     * @return the updated {@link ProjectDTO}
     */
    @Operation(
        summary = "Update a project",
        description = "Updates the project with the given ID using the provided information."
    )
    @PutMapping("/{id}")
    public ResponseEntity<ProjectDTO> update(final @PathVariable @NotNull UUID id,
                                             final @RequestBody @Valid ProjectRecord projectRecord,
                                             final @AuthenticationPrincipal UserPrincipal userPrincipal) {
        log.info("[{}] Received PUT request to update project with id {} and {}", userPrincipal.getEmail(), id,
            projectRecord);
        var project = service.updateProject(userPrincipal, id, projectRecord);

        return ResponseEntity.ok(mapper.toDto(project));
    }

    /**
     * Deletes a project by its ID.
     *
     * @param id            the UUID of the project to delete
     * @param userPrincipal the authenticated user performing the action
     * @return HTTP 204 No Content on success
     */
    @Operation(
        summary = "Delete a project",
        description = "Deletes the project with the specified ID."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMission(final @PathVariable @NotNull UUID id,
                                              final @AuthenticationPrincipal UserPrincipal userPrincipal) {
        log.info("[{}] Received DELETE request to delete project with id {}", userPrincipal.getEmail(), id);

        service.deleteProject(userPrincipal, id);

        return ResponseEntity.noContent().build();
    }
}
