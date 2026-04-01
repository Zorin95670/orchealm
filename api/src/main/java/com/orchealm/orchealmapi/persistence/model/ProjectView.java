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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity
@Table(name = "projects_view")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@QueryFilter
public class ProjectView extends AbstractEntity {

    /**
     * Primary key identifying the project.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "pro_id", nullable = false, updatable = false)
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
     * Parent project for sub-projects.
     */
    @Column(name = "parent")
    @FilterType(type = UUID.class)
    @QueryFilterField(type = UUID.class)
    private UUID parent;

    @Column(name = "parent_name")
    @FilterType(type = String.class)
    @QueryFilterField(type = String.class)
    private String parentName;

    @Column(name = "parent_organization")
    @FilterType(type = String.class)
    @QueryFilterField(type = String.class)
    private String parentOrganization;

    @Column(name = "parent_fullname")
    @FilterType(type = String.class)
    @QueryFilterField(type = String.class)
    private String parentFullName;

    /**
     * Organization or owner of the project.
     */
    @NotBlank
    @Size(max = 255)
    @Column(name = "organization", nullable = false)
    @FilterType(type = String.class)
    @QueryFilterField(type = String.class)
    private String organization;

    /**
     * Full name of the project.
     */
    @NotBlank
    @Size(max = 100)
    @Column(name = "name", nullable = false)
    @FilterType(type = String.class)
    @QueryFilterField(type = String.class)
    private String name;

    @Column(name = "fullname")
    @FilterType(type = String.class)
    @QueryFilterField(type = String.class)
    private String fullName;

    /**
     * Project display color stored as a hex color code (e.g. #RRGGBB).
     */
    @Column(name = "color", nullable = false)
    private String color;

    /**
     * Project text color using Quasar color tokens (e.g. white, black, grey-8, primary).
     */
    @Column(name = "text_color", nullable = false)
    private String textColor;

    /**
     * Indicates if this project is a master project.
     */
    @Column(name = "is_master", nullable = false)
    @FilterType(type = Boolean.class)
    @QueryFilterField(type = Boolean.class)
    private Boolean isMaster;

}
