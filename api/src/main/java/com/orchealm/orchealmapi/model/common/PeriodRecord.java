package com.orchealm.orchealmapi.model.common;

import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

public record PeriodRecord(@NotNull OffsetDateTime start,
                           @NotNull OffsetDateTime end) {
}
