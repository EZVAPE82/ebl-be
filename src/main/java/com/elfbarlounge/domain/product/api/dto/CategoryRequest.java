package com.elfbarlounge.domain.product.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryRequest(
        Long parentId,
        @NotBlank @Size(max = 80) String name,
        @NotBlank @Size(max = 80) String slug,
        Integer sortOrder,
        Boolean visible
) {}
