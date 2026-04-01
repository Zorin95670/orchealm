package com.orchealm.orchealmapi.model.team;

import jakarta.validation.constraints.NotNull;

public record TeamVisibilityRecord(@NotNull Boolean isPublic) {
}
