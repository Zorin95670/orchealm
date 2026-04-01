package com.orchealm.orchealmapi.persistence.attribute;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TstzRange {
    private OffsetDateTime start;
    private OffsetDateTime end;
}
