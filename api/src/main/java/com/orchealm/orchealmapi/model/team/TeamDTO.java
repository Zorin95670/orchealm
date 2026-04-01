package com.orchealm.orchealmapi.model.team;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

/**
 * Data Transfer Object representing a Team.
 * Used for API responses containing team details.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamDTO {

    /**
     * Unique key of the team, used as an external identifier.
     */
    @Schema(description = "Unique key of the team, used as an external identifier", example = "TEAM_ALPHA", requiredMode = Schema.RequiredMode.REQUIRED)
    private String key;

    /**
     * Human-readable name of the team.
     */
    @Schema(description = "Name of the team", example = "Alpha Team", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    /**
     * Optional description providing additional information about the team.
     */
    @Schema(description = "Optional description of the team", example = "This team handles internal projects")
    private String description;

    /**
     * Optional description providing additional information about the team.
     */
    @Schema(description = "Optional description of the team", example = "This team handles internal projects")
    private Boolean isPublic;

    /**
     * Identifier of the user who created this team.
     */
    @Schema(description = "Identifier of the user who created this team", example = "system")
    private String createdBy;

    /**
     * Identifier of the user who last updated this team.
     */
    @Schema(description = "Identifier of the user who last updated this team", example = "system")
    private String updatedBy;

    /**
     * Timestamp when this team was created.
     */
    @Schema(description = "Timestamp when this team was created", example = "2026-02-28T15:30:00Z")
    private OffsetDateTime insertDate;

    /**
     * Timestamp when this team was last updated.
     */
    @Schema(description = "Timestamp when this team was last updated", example = "2026-02-28T16:00:00Z")
    private OffsetDateTime updateDate;
}
