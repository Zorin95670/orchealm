package com.orchealm.orchealmapi.model.environment;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EnvironmentRecord(@NotBlank String teamKey,
                                @NotBlank @Size(max = 100) String name,
                                @NotBlank @Size(max = 10) String shortName,
                                @Min(0) int position) {
}
