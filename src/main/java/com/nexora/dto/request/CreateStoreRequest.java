package com.nexora.dto.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
public record CreateStoreRequest(
    @NotBlank String name,
    @NotBlank @Pattern(regexp = "^[a-z0-9-]+$", message = "Slug must be lowercase letters, numbers and hyphens only") String slug,
    String description
) {}
