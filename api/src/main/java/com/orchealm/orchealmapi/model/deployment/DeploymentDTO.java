package com.orchealm.orchealmapi.model.deployment;


import com.orchealm.orchealmapi.model.common.AbstractDTO;
import com.orchealm.orchealmapi.model.common.PeriodDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Data Transfer Object representing an environment")
public class DeploymentDTO extends AbstractDTO {
    @Schema(description = "Internal UUID of the environment", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;

    @Schema(description = "Key of the team this project belongs to", example = "TEAM_A")
    private String teamKey;
    private UUID environmentId;
    private UUID projectId;
    private String version;
    private String client;
    private PeriodDTO plannedPeriod;
    private PeriodDTO actualPeriod;
    private String status;
}
