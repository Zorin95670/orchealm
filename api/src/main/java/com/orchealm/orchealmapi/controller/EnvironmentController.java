package com.orchealm.orchealmapi.controller;

import com.orchealm.orchealmapi.model.common.PageResponse;
import com.orchealm.orchealmapi.model.environment.EnvironmentDTO;
import com.orchealm.orchealmapi.model.environment.EnvironmentMapper;
import com.orchealm.orchealmapi.model.environment.EnvironmentRecord;
import com.orchealm.orchealmapi.model.user.UserPrincipal;
import com.orchealm.orchealmapi.persistence.model.EnvironmentQueryFilterDto;
import com.orchealm.orchealmapi.service.EnvironmentService;
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

@Slf4j
@RestController
@RequestMapping("/environments")
@Tag(name = "Environment", description = "Endpoints to manage environments")
public class EnvironmentController implements PagedResponseStatusResolver {

    private final EnvironmentService service;
    private final EnvironmentMapper mapper;

    @Autowired
    public EnvironmentController(final EnvironmentService service, final EnvironmentMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @Operation(summary = "Create an environment", description = "Creates a new environment.")
    @PostMapping()
    public ResponseEntity<EnvironmentDTO> create(final @RequestBody @Valid EnvironmentRecord environmentRecord,
                                                 final @AuthenticationPrincipal UserPrincipal userPrincipal) {
        log.info("[{}] Received POST request to create environment with {}", userPrincipal.getEmail(),
            environmentRecord);

        var environment = service.createEnvironment(userPrincipal, environmentRecord);

        return ResponseEntity.status(HttpStatus.CREATED.value())
            .body(mapper.toDto(environment));
    }

    @Operation(summary = "Get environments", description =
        "Retrieves a list of environments with optional filtering and pagination.")
    @GetMapping()
    public ResponseEntity<PageResponse<EnvironmentDTO>> find(
        final @ModelAttribute EnvironmentQueryFilterDto filters,
        final @PageableDefault(page = 0, size = 10, sort = "position", direction = Sort.Direction.ASC) Pageable pageable,
        final @AuthenticationPrincipal UserPrincipal userPrincipal) {
        log.info("[{}] Received GET request to get environments with {}", userPrincipal.getEmail(), filters);

        Page<EnvironmentDTO> resources = service.findEnvironments(userPrincipal, filters, pageable)
            .map(mapper::toDto);

        return ResponseEntity.status(this.getStatus(resources)).body(PageResponse.of(resources, pageable));
    }

    @Operation(summary = "Get environment by ID", description =
        "Retrieves a environment by its ID.")
    @GetMapping("/{id}")
    public ResponseEntity<EnvironmentDTO> findById(final @PathVariable @NotNull UUID id,
                                                   final @AuthenticationPrincipal UserPrincipal userPrincipal) {
        log.info("[{}] Received GET request to get environment with id {}", userPrincipal.getEmail(), id);
        var environment = service.findEnvironmentById(userPrincipal, id);

        return ResponseEntity.ok(mapper.toDto(environment));
    }

    @Operation(summary = "Update a environment", description =
        "Updates the environment with the given ID.")
    @PutMapping("/{id}")
    public ResponseEntity<EnvironmentDTO> update(final @PathVariable @NotNull UUID id,
                                                 final @RequestBody @Valid EnvironmentRecord environmentRecord,
                                                 final @AuthenticationPrincipal UserPrincipal userPrincipal) {
        log.info("[{}] Received PUT request to update environment with id {} and {}", userPrincipal.getEmail(), id,
            environmentRecord);
        var environment = service.updateEnvironment(userPrincipal, id, environmentRecord);

        return ResponseEntity.ok(mapper.toDto(environment));
    }

    @Operation(summary = "Delete a environment", description =
        "Deletes the environment with the given ID for the authenticated user.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMission(final @PathVariable @NotNull UUID id,
                                              final @AuthenticationPrincipal UserPrincipal userPrincipal) {
        log.info("[{}] Received DELETE request to delete environment with id {}", userPrincipal.getEmail(), id);

        service.deleteEnvironment(userPrincipal, id);

        return ResponseEntity.noContent().build();
    }
}
