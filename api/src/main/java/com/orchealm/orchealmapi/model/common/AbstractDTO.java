package com.orchealm.orchealmapi.model.common;

import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;

@MappedSuperclass
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public abstract class AbstractDTO {

    private String createdBy;
    private String updatedBy;
    private OffsetDateTime insertDate;
    private OffsetDateTime updateDate;
}
