package com.orchealm.orchealmapi.model.environment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Schema(description = "Data Transfer Object representing an environment")
public class EnvironmentDTO {
    /**
     * Internal id.
     */
    @Schema(description = "Internal UUID of the environment", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;

    @Schema(description = "Key of the team this project belongs to", example = "TEAM_A")
    private String teamKey;

    @Schema(description = "Name of the environment", example = "Production")
    private String name;

    @Schema(description = "ShortName of the environment", example = "PROD")
    private String shortName;

    // TODO
    @Schema(description = "ShortName of the environment", example = "PROD")
    private int position;

    @Schema(description = "Creation date of the environment", example = "1970-01-01T00:00:00+00:00")
    private OffsetDateTime insertDate;

    @Schema(description = "The last update date of the environment", example = "1970-01-01T00:00:00+00:00")
    private OffsetDateTime updateDate;

}
