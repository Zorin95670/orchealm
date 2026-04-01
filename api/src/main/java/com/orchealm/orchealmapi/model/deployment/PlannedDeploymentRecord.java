package com.orchealm.orchealmapi.model.deployment;

import com.orchealm.orchealmapi.model.common.PeriodRecord;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PlannedDeploymentRecord(@NotBlank String teamKey,
                                      @NotBlank String environment,
                                      @NotBlank String projectOrganization,
                                      @NotBlank String projectName,
                                      @NotBlank String version,
                                      @NotBlank String client,
                                      @NotNull PeriodRecord plannedPeriod) {
}
