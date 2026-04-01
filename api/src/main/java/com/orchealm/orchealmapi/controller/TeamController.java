package com.orchealm.orchealmapi.controller;

import com.orchealm.orchealmapi.model.common.PageResponse;
import com.orchealm.orchealmapi.model.error.ApiException;
import com.orchealm.orchealmapi.model.error.ErrorReference;
import com.orchealm.orchealmapi.model.team.TeamDTO;
import com.orchealm.orchealmapi.model.team.TeamMapper;
import com.orchealm.orchealmapi.model.team.TeamRecord;
import com.orchealm.orchealmapi.model.team.TeamVisibilityRecord;
import com.orchealm.orchealmapi.model.user.UserPrincipal;
import com.orchealm.orchealmapi.persistence.model.TeamQueryFilterDto;
import com.orchealm.orchealmapi.service.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller exposing endpoints for managing teams.
 *
 * <p>Supports CRUD operations with RLS enforcement via {@link UserPrincipal}.
 * Each endpoint logs the authenticated user and the performed action.
 */
@Slf4j
@RestController
@RequestMapping("/teams")
@Tag(name = "Team", description = "Endpoints to create, read, update, and delete teams")
public class TeamController implements PagedResponseStatusResolver {

    /**
     * Service providing team management operations.
     */
    private final TeamService service;

    /**
     * Mapper for converting between Team entities and {@link TeamDTO}.
     */
    private final TeamMapper mapper;

    /**
     * Constructs the TeamsController with required dependencies.
     *
     * @param service the team service
     * @param mapper  the team mapper
     */
    @Autowired
    public TeamController(final TeamService service, final TeamMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    /**
     * Creates a new team.
     *
     * @param teamRecord    the team data to create
     * @param userPrincipal the authenticated user performing the action
     * @return the created {@link TeamDTO} with HTTP status 201
     */
    @Operation(
        summary = "Create a team",
        description = "Creates a new team with the provided information."
    )
    @PostMapping()
    public ResponseEntity<TeamDTO> create(final @RequestBody @Valid TeamRecord teamRecord,
                                          final @AuthenticationPrincipal UserPrincipal userPrincipal) {
        log.info("[{}] Received POST request to create team with {}", userPrincipal.getEmail(), teamRecord);

        var team = service.createTeam(userPrincipal, teamRecord);

        return ResponseEntity.status(HttpStatus.CREATED.value())
            .body(mapper.toDto(team));
    }

    @Operation(
        summary = "Create a team",
        description = "Creates a new team with the provided information."
    )
    @PutMapping("/{key}/visibility")
    public ResponseEntity<TeamDTO> changeVisibility(final @PathVariable @NotBlank String key,
                                                    final @Valid @RequestBody TeamVisibilityRecord visibilityRecord,
                                                    final @AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (!userPrincipal.isAdmin()) {
            throw new ApiException(ErrorReference.unauthorized());
        }

        log.info("[{}] Received PUT request to change visibility of team {} with {}", userPrincipal.getEmail(), key,
            visibilityRecord);

        var team = service.changeVisibility(userPrincipal, key, visibilityRecord);

        return ResponseEntity.ok(mapper.toDto(team));
    }

    /**
     * Retrieves a paginated list of teams with optional filters.
     *
     * @param filters       filter criteria as a map of field name to values
     * @param pageable      pagination and sorting information
     * @param userPrincipal the authenticated user performing the action
     * @return a paginated list of {@link TeamDTO}
     */
    @Operation(
        summary = "Get teams",
        description = "Retrieves a paginated list of teams, optionally filtered by field values."
    )
    @GetMapping()
    public ResponseEntity<PageResponse<TeamDTO>> find(
        final TeamQueryFilterDto filters,
        final @PageableDefault(page = 0, size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable,
        final @AuthenticationPrincipal UserPrincipal userPrincipal) {
        log.info("[{}] Received GET request to get teams with {}", userPrincipal.getEmail(), filters);

        Page<TeamDTO> resources = service.findTeams(userPrincipal, filters, pageable)
            .map(mapper::toDto);

        return ResponseEntity.status(this.getStatus(resources)).body(PageResponse.of(resources, pageable));
    }

    /**
     * Retrieves a team by its key.
     *
     * @param key           the key of the team
     * @param userPrincipal the authenticated user performing the action
     * @return the team as {@link TeamDTO}
     */
    @Operation(
        summary = "Get team by key",
        description = "Retrieves a specific team by its key."
    )
    @GetMapping("/{key}")
    public ResponseEntity<TeamDTO> findByKey(final @PathVariable @NotBlank String key,
                                             final @AuthenticationPrincipal UserPrincipal userPrincipal) {
        log.info("[{}] Received GET request to get team with key {}", userPrincipal.getEmail(), key);
        var team = service.findTeamByKey(userPrincipal, key);

        return ResponseEntity.ok(mapper.toDto(team));
    }

    /**
     * Updates an existing team.
     *
     * @param key           the key of the team to update
     * @param teamRecord    the updated team data
     * @param userPrincipal the authenticated user performing the action
     * @return the updated {@link TeamDTO}
     */
    @Operation(
        summary = "Update a team",
        description = "Updates the team with the given key using the provided information."
    )
    @PutMapping("/{key}")
    public ResponseEntity<TeamDTO> update(final @PathVariable @NotNull String key,
                                          final @RequestBody @Valid TeamRecord teamRecord,
                                          final @AuthenticationPrincipal UserPrincipal userPrincipal) {
        log.info("[{}] Received PUT request to update team with key {} and {}", userPrincipal.getEmail(), key,
            teamRecord);
        var team = service.updateTeam(userPrincipal, key, teamRecord);

        return ResponseEntity.ok(mapper.toDto(team));
    }

    /**
     * Deletes a team by its key.
     *
     * @param key           the key of the team to delete
     * @param userPrincipal the authenticated user performing the action
     * @return HTTP 204 No Content on success
     */
    @Operation(
        summary = "Delete a team",
        description = "Deletes the team with the specified key."
    )
    @DeleteMapping("/{key}")
    public ResponseEntity<Void> deleteMission(final @PathVariable @NotNull String key,
                                              final @AuthenticationPrincipal UserPrincipal userPrincipal) {
        log.info("[{}] Received DELETE request to delete team with key {}", userPrincipal.getEmail(), key);

        service.deleteTeam(userPrincipal, key);

        return ResponseEntity.noContent().build();
    }
}
