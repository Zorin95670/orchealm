package com.orchealm.orchealmapi.model.project;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Schema(description = "Data Transfer Object representing a project")
public class ProjectViewDTO {

    @Schema(description = "Internal UUID of the project", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;

    @Schema(description = "Key of the team this project belongs to", example = "TEAM_A")
    private String teamKey;

    @Schema(description = "UUID of the parent master project, null if this project is a master", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID parent;

    @Schema(description = "Name of the parent project", example = "My Parent Project")
    private String parentName;

    @Schema(description = "Organization or owner of the parent project", example = "Acme Corp")
    private String parentOrganization;

    @Schema(description = "Organization and name of the parent project", example = "Acme Corp - My Parent Project")
    private String parentFullName;

    @Schema(description = "Organization or owner of the project", example = "Acme Corp")
    private String organization;

    @Schema(description = "Name of the project", example = "My Project")
    private String name;

    @Schema(description = "Organization and name of the project", example = "Acme Corp - My Project")
    private String fullName;

    /**
     * Project display color stored as a hex color code (e.g. #RRGGBB)
     */
    @Schema(description = "Project display color stored as a hex color code", example = "#001122")
    private String color;

    /**
     * Project text color using Quasar color tokens (e.g. white, black, grey-8, primary).
     */
    @Schema(description = "Project text color using Quasar color tokens", examples = {"white", "black", "primary",
        "grey-7"})
    private String textColor;

    @Schema(description = "Whether this project is a master project", example = "true")
    private boolean master;

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