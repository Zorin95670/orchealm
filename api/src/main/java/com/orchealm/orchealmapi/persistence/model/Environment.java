package com.orchealm.orchealmapi.persistence.model;

import io.github.zorin95670.predicate.FilterType;
import io.github.zorin95670.processor.annotation.QueryFilter;
import io.github.zorin95670.processor.annotation.QueryFilterField;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

/**
 * Entity representing an environment (e.g., Development, Integration, Pre-production, Production)
 * where projects can be deployed.
 */
@Entity
@Table(name = "environments")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@QueryFilter
public class Environment extends AbstractEntity {

    /**
     * Primary key identifying the environment.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "env_id", updatable = false, nullable = false)
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
     * Full name of the environment.
     * Example: "Development", "Production".
     */
    @NotBlank
    @Size(max = 100)
    @Column(name = "name", nullable = false, unique = true)
    @FilterType(type = String.class)
    @QueryFilterField(type = String.class)
    private String name;

    /**
     * Short abbreviation for the environment.
     * Example: "DEV", "PROD".
     */
    @NotBlank
    @Size(max = 10)
    @Column(name = "shortname", nullable = false, unique = true)
    @FilterType(type = String.class)
    @QueryFilterField(type = String.class)
    private String shortName;

    /**
     * Numeric order for display or sorting purposes.
     */
    @Min(0)
    @Column(name = "position", nullable = false)
    @FilterType(type = Integer.class)
    @QueryFilterField(type = Integer.class)
    private Integer position;
}
