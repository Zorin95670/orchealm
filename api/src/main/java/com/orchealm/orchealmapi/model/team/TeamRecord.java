package com.orchealm.orchealmapi.model.team;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Record representing a Team.
 * Used for API requests and responses when creating or retrieving team information.
 *
 * @param key         Unique key of the team, used as an external identifier.
 *                    Must consist of uppercase letters and underscores.
 * @param name        Human-readable name of the team.
 * @param description Optional description providing additional information about the team.
 */
@Schema(description = "Data Transfer Object representing a team.")
public record TeamRecord(
    @Schema(description = "Unique key of the team, uppercase letters and underscores only", example = "TEAM_ALPHA", requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(max = 255, min = 1) @Pattern(regexp = "^[A-Z][A-Z0-9_]*[A-Z0-9]$") String key,
    @Schema(description = "Human-readable name of the team", example = "Alpha Team", requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(max = 100, min = 1) @NotBlank String name,
    @Schema(description = "Optional description of the team", example = "This team handles internal projects", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Size(max = 255) String description) {
}
