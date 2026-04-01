package com.orchealm.orchealmapi.model.deployment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record DeploymentRecord(@NotBlank String teamKey,
                               @NotBlank String environment,
                               @NotBlank String projectOrganization,
                               @NotBlank String projectName,
                               String version,
                               @NotBlank String client,
                               @Pattern(regexp = "^PLANNED|IN_PROGRESS|COMPLETED|CANCELLED|FAILED$") String status) {
}
