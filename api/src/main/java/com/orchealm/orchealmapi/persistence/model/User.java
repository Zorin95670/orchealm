package com.orchealm.orchealmapi.persistence.model;

import io.github.zorin95670.predicate.FilterType;
import io.github.zorin95670.processor.annotation.QueryFilter;
import io.github.zorin95670.processor.annotation.QueryFilterField;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.UUID;


/**
 * Entity representing an application user.
 * Users are linked to external identity providers and can be assigned roles.
 */
@Entity
@Table(name = "users")
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@QueryFilter
public class User {

    /**
     * Primary key identifying the user.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "usr_id", nullable = false, updatable = false)
    @FilterType(type = UUID.class)
    @QueryFilterField(type = UUID.class)
    private UUID id;

    /**
     * External identifier for the user (from SSO or OIDC token).
     */
    @NotBlank
    @Column(name = "external_id", nullable = false, unique = true)
    @FilterType(type = String.class)
    @QueryFilterField(type = String.class)
    private String externalId;

    /**
     * First name and last name of the user.
     */
    @Size(max = 255)
    @Column(name = "name")
    @FilterType(type = String.class)
    @QueryFilterField(type = String.class)
    private String name;

    /**
     * Email address of the user.
     */
    @Email
    @Size(max = 255)
    @Column(name = "email", unique = true)
    @FilterType(type = String.class)
    @QueryFilterField(type = String.class)
    private String email;

    /**
     * The creation date of this entity.
     */
    @Column(name = "insert_date", updatable = false)
    @FilterType(type = Date.class)
    @QueryFilterField(type = Date.class)
    private OffsetDateTime insertDate;

    /**
     * The last update date of this entity.
     */
    @Column(name = "update_date")
    @FilterType(type = Date.class)
    @Version
    @QueryFilterField(type = Date.class)
    private OffsetDateTime updateDate;

    @PrePersist
    protected void prePersist() {
        this.insertDate = OffsetDateTime.now();
    }

    @PreUpdate
    protected void preUpdate() {
        this.updateDate = OffsetDateTime.now();
    }
}