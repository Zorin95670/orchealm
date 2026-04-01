package com.orchealm.orchealmapi.model.deployment;


import com.orchealm.orchealmapi.model.common.AbstractDTO;
import com.orchealm.orchealmapi.model.common.PeriodDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Data Transfer Object representing a summarized view of the latest deployment
 * for a given (project, environment, client) tuple.
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(name = "LastDeployment",
    description = "Represents the latest deployment state for a project in a specific environment and client context")
public class DeploymentViewDTO extends AbstractDTO {

    /**
     * Unique identifier of the deployment.
     */
    @Schema(description = "Unique identifier of the deployment")
    private UUID id;

    /**
     * Key identifying the team owning the deployment.
     */
    @Schema(description = "Key identifying the team owning the deployment")
    private String teamKey;

    /**
     * Identifier of the deployed project.
     */
    @Schema(description = "Identifier of the deployed project")
    private UUID projectId;

    private PeriodDTO plannedPeriod;
    private OffsetDateTime plannedPeriodStart;
    private OffsetDateTime plannedPeriodEnd;
    private PeriodDTO actualPeriod;
    private OffsetDateTime actualPeriodStart;
    private OffsetDateTime actualPeriodEnd;

    private boolean delayed;

    /**
     * Identifier of the parent (master) project, if any.
     */
    @Schema(description = "Identifier of the parent (master) project, if any")
    private UUID masterProjectId;

    /**
     * Display name of the deployed project.
     */
    @Schema(description = "Display name of the deployed project")
    private String projectName;

    /**
     * Display name of the parent (master) project.
     */
    @Schema(description = "Display name of the parent (master) project")
    private String masterProjectName;

    /**
     * Project display color stored as a hex color code (e.g. #RRGGBB)
     */
    @Schema(description = "Project display color stored as a hex color code", example = "#001122")
    private String projectColor;

    /**
     * Project text color using Quasar color tokens (e.g. white, black, grey-8, primary).
     */
    @Schema(description = "Project text color using Quasar color tokens", examples = {"white", "black", "primary",
        "grey-7"})
    private String projectTextColor;

    /**
     * Full name of the deployment environment.
     */
    @Schema(description = "Full name of the deployment environment")
    private String environmentName;

    /**
     * Short code or identifier of the deployment environment.
     */
    @Schema(description = "Short code or identifier of the deployment environment")
    private String environmentShortName;

    /**
     * Numeric order for display or sorting purposes of the deployment environment.
     */
    @Schema(description = "Numeric order for display or sorting purposes of the deployment environment")
    private Integer environmentPosition;

    /**
     * Version of the deployed artifact.
     */
    @Schema(description = "Version of the deployed artifact")
    private String version;

    /**
     * Client for whom the deployment was performed.
     */
    @Schema(description = "Client for whom the deployment was performed")
    private String client;

    /**
     * Current lifecycle status of the deployment.
     */
    @Schema(
        description = "Current lifecycle status of the deployment",
        allowableValues = {"PLANNED", "IN_PROGRESS", "COMPLETED", "CANCELLED", "FAILED"}
    )
    private String status;

    /**
     * Current change state of the deployment based on its update date.
     */
    @Schema(
        description = "Current change state of the deployment based on its update date",
        allowableValues = {"HOT", "NEW"}
    )
    private String state;
}
