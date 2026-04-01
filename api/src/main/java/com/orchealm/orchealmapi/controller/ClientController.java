package com.orchealm.orchealmapi.controller;

import com.orchealm.orchealmapi.model.common.PageResponse;
import com.orchealm.orchealmapi.model.user.UserPrincipal;
import com.orchealm.orchealmapi.persistence.model.DeploymentQueryFilterDto;
import com.orchealm.orchealmapi.service.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/clients")
@Tag(name = "Client", description = "Endpoints to manage clients")
public class ClientController implements PagedResponseStatusResolver {

    private final ClientService service;

    /**
     * Constructs the ClientController with required dependencies.
     *
     * @param service the clientservice
     */
    @Autowired
    public ClientController(final ClientService service) {
        this.service = service;
    }

    @Operation(summary = "Get clients", description =
        "Retrieves a list of client names with optional filtering and pagination.")
    @GetMapping()
    public ResponseEntity<PageResponse<String>> find(
        final @ModelAttribute DeploymentQueryFilterDto filters,
        final @PageableDefault(page = 0, size = 10, sort = "client", direction = Sort.Direction.ASC) Pageable pageable,
        final @AuthenticationPrincipal UserPrincipal userPrincipal) {
        log.info("[{}] Received GET request to get clients with {}", userPrincipal.getEmail(), filters);

        Page<String> resources = service.findClient(userPrincipal, filters, pageable);

        return ResponseEntity.status(this.getStatus(resources)).body(PageResponse.of(resources, pageable));
    }
}

