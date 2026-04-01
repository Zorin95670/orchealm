package com.orchealm.orchealmapi.persistence.model;

import io.github.zorin95670.predicate.FilterType;
import io.github.zorin95670.processor.annotation.QueryFilter;
import io.github.zorin95670.processor.annotation.QueryFilterField;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

/**
 * Entity representing a team within the application.
 * Teams are used to group projects, environments, and manage access permissions.
 */
@Entity
@Table(name = "teams")
@Cacheable(false)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@QueryFilter
public class Team extends AbstractEntity {

    /**
     * Primary key identifying the team.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "tea_id", nullable = false, updatable = false)
    @FilterType(type = UUID.class)
    @QueryFilterField(type = UUID.class, description = "Id of team")
    private UUID id;

    /**
     * Unique key of the team, used as an external identifier.
     * Must consist of uppercase letters and underscores.
     */
    @NotBlank
    @Pattern(regexp = "^[A-Z][A-Z0-9_]*[A-Z0-9]$")
    @Size(max = 255)
    @Column(name = "key", nullable = false, unique = true)
    @FilterType(type = String.class)
    @QueryFilterField(type = String.class, description = "key of team")
    private String key;

    /**
     * Human-readable name of the team.
     */
    @Size(max = 100)
    @Column(name = "name")
    @FilterType(type = String.class)
    @QueryFilterField(type = String.class, description = "Name of team")
    private String name;

    /**
     * Optional description providing additional information about the team.
     */
    @Size(max = 255)
    @Column(name = "description")
    @FilterType(type = String.class)
    @QueryFilterField(type = String.class, description = "Description of team")
    private String description;

    /**
     * Optional description providing additional information about the team.
     */
    @Column(name = "is_public")
    @FilterType(type = Boolean.class)
    @QueryFilterField(type = Boolean.class, description = "Description of team")
    private Boolean isPublic;
}