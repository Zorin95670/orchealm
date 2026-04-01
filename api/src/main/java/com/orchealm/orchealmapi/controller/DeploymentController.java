package com.orchealm.orchealmapi.controller;

import com.orchealm.orchealmapi.model.common.PageResponse;
import com.orchealm.orchealmapi.model.deployment.DeploymentDTO;
import com.orchealm.orchealmapi.model.deployment.DeploymentMapper;
import com.orchealm.orchealmapi.model.deployment.DeploymentRecord;
import com.orchealm.orchealmapi.model.deployment.DeploymentViewDTO;
import com.orchealm.orchealmapi.model.deployment.PlannedDeploymentRecord;
import com.orchealm.orchealmapi.model.user.UserPrincipal;
import com.orchealm.orchealmapi.persistence.model.DeploymentViewQueryFilterDto;
import com.orchealm.orchealmapi.persistence.model.LastDeploymentViewQueryFilterDto;
import com.orchealm.orchealmapi.service.DeploymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@RestController
@RequestMapping("/deployments")
@Tag(name = "Deployment", description = "Endpoints to create, read, update, and delete deployments")
public class DeploymentController implements PagedResponseStatusResolver {

    /**
     * Service providing deployment management operations.
     */
    private final DeploymentService service;

    /**
     * Mapper for converting between Deployment entities and {@link DeploymentDTO}.
     */
    private final DeploymentMapper mapper;

    /**
     * Constructs the DeploymentsController with required dependencies.
     *
     * @param service the deployment service
     * @param mapper  the deployment mapper
     */
    @Autowired
    public DeploymentController(final DeploymentService service, final DeploymentMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    /**
     * Creates a new deployment.
     *
     * @param deploymentRecord the deployment data to create
     * @param userPrincipal    the authenticated user performing the action
     * @return the created {@link DeploymentDTO} with HTTP status 201
     */
    @Operation(
        summary = "Create a deployment",
        description = "Creates a new deployment with the provided information."
    )
    @PostMapping()
    public ResponseEntity<DeploymentDTO> planned(
        final @RequestBody @Valid PlannedDeploymentRecord plannedDeploymentRecord,
        final @AuthenticationPrincipal UserPrincipal userPrincipal) {
        log.info("[{}] Received POST request to plan deployment with {}", userPrincipal.getEmail(),
            plannedDeploymentRecord);

        var deployment = service.planDeployment(userPrincipal, plannedDeploymentRecord);

        return ResponseEntity.status(HttpStatus.CREATED.value())
            .body(mapper.toDto(deployment));
    }

    @Operation(
        summary = "Create a deployment",
        description = "Creates a new deployment with the provided information."
    )
    @PostMapping("/start")
    public ResponseEntity<DeploymentDTO> start(
        final @RequestBody @Valid DeploymentRecord deploymentRecord,
        final @AuthenticationPrincipal UserPrincipal userPrincipal) {
        log.info("[{}] Received POST request to start deployment with {}", userPrincipal.getEmail(),
            deploymentRecord);

        var deployment = service.startDeployment(userPrincipal, deploymentRecord);

        return ResponseEntity.status(HttpStatus.CREATED.value())
            .body(mapper.toDto(deployment));
    }

    @Operation(
        summary = "Create a deployment",
        description = "Creates a new deployment with the provided information."
    )
    @PostMapping("/stop")
    public ResponseEntity<DeploymentDTO> stop(
        final @RequestBody @Valid DeploymentRecord deploymentRecord,
        final @AuthenticationPrincipal UserPrincipal userPrincipal) {
        log.info("[{}] Received POST request to stop deployment with {}", userPrincipal.getEmail(),
            deploymentRecord);

        var deployment = service.stopDeployment(userPrincipal, deploymentRecord);

        return ResponseEntity.status(HttpStatus.CREATED.value())
            .body(mapper.toDto(deployment));
    }

    @Operation(summary = "Get last deployments", description =
        "Retrieves a list of environments with optional filtering and pagination.")
    @GetMapping("/last")
    public ResponseEntity<PageResponse<DeploymentViewDTO>> find(
        final @ModelAttribute LastDeploymentViewQueryFilterDto filters,
        final @PageableDefault(page = 0, size = 10, sort = "environmentName", direction = Sort.Direction.ASC) Pageable pageable,
        final @AuthenticationPrincipal UserPrincipal userPrincipal) {
        log.info("[{}] Received GET request to get last deployments with {}", userPrincipal.getEmail(), filters);

        Page<DeploymentViewDTO> resources = service.getLastDeployments(userPrincipal, filters, pageable)
            .map(mapper::toDto);

        return ResponseEntity.status(this.getStatus(resources)).body(PageResponse.of(resources, pageable));
    }

    @Operation(summary = "Get deployments", description =
        "Retrieves a list of environments with optional filtering and pagination.")
    @GetMapping("")
    public ResponseEntity<PageResponse<DeploymentViewDTO>> find(
        final @ModelAttribute DeploymentViewQueryFilterDto filters,
        final @PageableDefault(page = 0, size = 10, sort = "updateDate", direction = Sort.Direction.DESC) Pageable pageable,
        final @AuthenticationPrincipal UserPrincipal userPrincipal) {
        log.info("[{}] Received GET request to get deployments with {}", userPrincipal.getEmail(), filters);
        System.out.println(new ObjectMapper().writeValueAsString(filters));

        Page<DeploymentViewDTO> resources = service.getDeployments(userPrincipal, filters, pageable)
            .map(mapper::toDto);

        return ResponseEntity.status(this.getStatus(resources)).body(PageResponse.of(resources, pageable));
    }
}
