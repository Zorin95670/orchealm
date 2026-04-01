package com.orchealm.orchealmapi.persistence.model;

import io.github.zorin95670.predicate.FilterType;
import io.github.zorin95670.processor.annotation.QueryFilterField;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;
import java.util.Date;

/**
 * Abstract entity with default fields (InsertDate and UpdateDate).
 */
@MappedSuperclass
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public abstract class AbstractEntity {

    /**
     * Identifier of the user who created this entity.
     * Should match the userId from the JWT or application context.
     */
    @Column(name = "created_by", nullable = false, updatable = false)
    @FilterType(type = String.class)
    @QueryFilterField(type = String.class)
    private String createdBy;

    /**
     * Identifier of the user who last updated this entity.
     * Should match the userId from the JWT or application context.
     */
    @Column(name = "updated_by", nullable = false)
    @FilterType(type = String.class)
    @QueryFilterField(type = String.class)
    private String updatedBy;

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
    @QueryFilterField(type = Date.class)
    @Version
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
