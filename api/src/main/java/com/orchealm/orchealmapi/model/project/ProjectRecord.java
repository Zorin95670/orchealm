package com.orchealm.orchealmapi.model.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record ProjectRecord(UUID parent,
                            @NotBlank String teamKey,
                            @NotBlank @Size(max = 255) String organization,
                            @NotBlank @Size(max = 100) String name,
                            @NotBlank @Pattern(regexp = "^#[0-9a-fA-F]{6}$") String color,
                            @NotBlank String textColor) {
}
