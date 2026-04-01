package com.orchealm.orchealmapi.persistence.model;

import io.github.zorin95670.predicate.FilterType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Entity representing dynamic application configuration parameters.
 * Each row is a key-value pair configurable via frontend or backend.
 */
@Entity
@Table(name = "application_configuration")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationConfiguration extends AbstractEntity {

    /**
     * Primary key identifying the configuration entry.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "apc_id", nullable = false, updatable = false)
    @FilterType(type = UUID.class)
    private UUID id;

    /**
     * Unique key identifying the configuration property.
     */
    @NotBlank
    @Size(max = 255)
    @Column(name = "key", nullable = false, unique = true)
    @FilterType(type = String.class)
    private String key;

    /**
     * The value of the configuration parameter stored as text.
     * Can contain JSON, YAML, or a simple string.
     */
    @NotNull
    @Column(name = "value", nullable = false)
    private String value;
}