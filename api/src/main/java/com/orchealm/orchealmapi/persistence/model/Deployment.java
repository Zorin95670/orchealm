package com.orchealm.orchealmapi.persistence.model;

import com.orchealm.orchealmapi.persistence.attribute.TstzRange;
import com.orchealm.orchealmapi.persistence.type.TstzRangeType;
import io.github.zorin95670.predicate.FilterType;
import io.github.zorin95670.processor.annotation.QueryFilter;
import io.github.zorin95670.processor.annotation.QueryFilterField;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;

import java.util.UUID;

/**
 * Entity representing a deployment of a project to a specific environment.
 * Contains planned and actual start/end dates, client, version, and status.
 */
@Entity
@Table(name = "deployments")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@QueryFilter
public class Deployment extends AbstractEntity {

    /**
     * Primary key identifying the deployment.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "dpl_id", nullable = false, updatable = false)
    @FilterType(type = UUID.class)
    @QueryFilterField(type = UUID.class)
    private UUID id;

    @NotBlank
    @Size(max = 255)
    @Column(name = "team_key", nullable = false)
    @FilterType(type = String.class)
    @QueryFilterField(type = String.class)
    private String teamKey;

    /**
     * Environment where the project is deployed.
     */
    @Column(name = "env_id", nullable = false, updatable = false)
    @FilterType(type = UUID.class)
    @QueryFilterField(type = UUID.class)
    private UUID environmentId;

    /**
     * Project being deployed.
     */
    @Column(name = "pro_id", nullable = false, updatable = false)
    @FilterType(type = UUID.class)
    @QueryFilterField(type = UUID.class)
    private UUID projectId;

    /**
     * Version of the deployed project.
     */
    @NotBlank
    @Size(max = 100)
    @Column(name = "version", nullable = false)
    @FilterType(type = String.class)
    @QueryFilterField(type = String.class)
    private String version;

    /**
     * Client for whom the deployment is performed.
     */
    @NotBlank
    @Size(max = 100)
    @Column(name = "client", nullable = false)
    @FilterType(type = String.class)
    @QueryFilterField(type = String.class)
    private String client;

    @Column(name = "planned_period", columnDefinition = "tstzrange")
    @Type(TstzRangeType.class)
    private TstzRange plannedPeriod;

    @Column(name = "actual_period", columnDefinition = "tstzrange")
    @Type(TstzRangeType.class)
    private TstzRange actualPeriod;

    /**
     * Current status of the deployment.
     */
    @Column(name = "status", nullable = false)
    @QueryFilterField(type = String.class)
    @FilterType(type = String.class)
    private String status;

    /**
     * Deployment status enum.
     */
    public enum DeploymentStatus {
        PLANNED,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED,
        FAILED
    }
}
