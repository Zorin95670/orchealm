package com.orchealm.orchealmapi.persistence.model;

import com.orchealm.orchealmapi.persistence.attribute.TstzRange;
import com.orchealm.orchealmapi.persistence.type.TstzRangeType;
import io.github.zorin95670.predicate.FilterType;
import io.github.zorin95670.processor.annotation.QueryFilter;
import io.github.zorin95670.processor.annotation.QueryFilterField;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

/**
 * Entity representing the last deployment of a project to a specific environment and client.
 */
@Entity
@Table(name = "last_deployments_view")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@QueryFilter
public class LastDeploymentView extends AbstractEntity {

    /**
     * Unique identifier of the deployment.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "dpl_id")
    @FilterType(type = UUID.class)
    @QueryFilterField(type = UUID.class)
    private UUID id;

    /**
     * Identifier of the team owning the deployment.
     */
    @FilterType(type = String.class)
    @QueryFilterField(type = String.class)
    private String teamKey;

    /**
     * Identifier of the deployed project.
     */
    @Column(name = "pro_id")
    @FilterType(type = UUID.class)
    @QueryFilterField(type = UUID.class)
    private UUID projectId;

    /**
     * Identifier of the parent (master) project, if any.
     */
    @Column(name = "master_pro_id")
    @FilterType(type = UUID.class)
    @QueryFilterField(type = UUID.class)
    private UUID masterProjectId;

    /**
     * Display name of the deployed project.
     */
    @Column(name = "project_name")
    @FilterType(type = String.class)
    @QueryFilterField(type = String.class)
    private String projectName;

    @Column(name = "planned_period", columnDefinition = "tstzrange")
    @Type(TstzRangeType.class)
    private TstzRange plannedPeriod;

    @Column(name = "actual_period", columnDefinition = "tstzrange")
    @Type(TstzRangeType.class)
    private TstzRange actualPeriod;

    @Column(name = "delayed")
    @FilterType(type = Boolean.class)
    @QueryFilterField(type = Boolean.class)
    private boolean delayed;

    /**
     * Display name of the parent (master) project.
     */
    @Column(name = "master_project_name")
    @FilterType(type = String.class)
    @QueryFilterField(type = String.class)
    private String masterProjectName;

    /**
     * Project display color stored as a hex color code (e.g. #RRGGBB).
     */
    @Column(name = "project_color", nullable = false)
    private String projectColor;

    /**
     * Project text color using Quasar color tokens (e.g. white, black, grey-8, primary).
     */
    @Column(name = "project_text_color", nullable = false)
    private String projectTextColor;

    /**
     * Full name of the deployment environment.
     */
    @Column(name = "environment_name")
    @FilterType(type = String.class)
    @QueryFilterField(type = String.class)
    private String environmentName;

    /**
     * Short name or code of the deployment environment.
     */
    @Column(name = "environment_shortname")
    @FilterType(type = String.class)
    @QueryFilterField(type = String.class)
    private String environmentShortName;

    /**
     * Numeric order for display or sorting purposes of the deployment environment.
     */
    @Column(name = "environment_position")
    @FilterType(type = Integer.class)
    @QueryFilterField(type = Integer.class)
    private Integer environmentPosition;

    /**
     * Version identifier of the deployed artifact.
     */
    @Column(name = "version")
    @FilterType(type = String.class)
    @QueryFilterField(type = String.class)
    private String version;

    /**
     * Client for whom the deployment was performed.
     */
    @Column(name = "client")
    @FilterType(type = String.class)
    @QueryFilterField(type = String.class)
    private String client;

    /**
     * Current lifecycle status of the deployment.
     */
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "status", nullable = false)
    @QueryFilterField(type = String.class)
    private Deployment.DeploymentStatus status;

    /**
     * Current change state of the deployment based on its update date.
     */
    @Column(name = "state")
    @FilterType(type = String.class)
    @QueryFilterField(type = String.class)
    private String state;
}
