package com.elfbarlounge.domain.product.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BrandRequest(
        @NotBlank @Size(max = 80) String name,
        @NotBlank @Size(max = 80) String slug,
        @Size(max = 1000) String logoUrl,
        Integer sortOrder
) {}
