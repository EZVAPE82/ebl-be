package com.elfbarlounge.domain.product.api.dto;

import com.elfbarlounge.domain.product.domain.ProductImage;
import com.elfbarlounge.domain.product.domain.ProductStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ProductUpsertRequest(
        Long categoryId,
        Long brandId,

        @NotBlank
        @Size(max = 200)
        String name,

        @NotBlank
        @Size(max = 200)
        String slug,

        @Size(max = 5000)
        String description,

        @Size(max = 2000)
        String compatibilityInfo,

        @NotNull
        @PositiveOrZero
        Long price,

        ProductStatus status,

        @Size(max = 1000)
        String thumbnailUrl,

        @Min(0)
        Integer stockThreshold,

        @Valid
        List<OptionInput> options,

        @Valid
        List<ImageInput> images
) {
    public record OptionInput(
            @NotBlank @Size(max = 40) String optionGroup,
            @NotBlank @Size(max = 80) String optionValue,
            @NotNull Long priceDelta,
            @Min(0) Integer stock,
            Boolean required,
            Integer sortOrder,
            Boolean visible
    ) {}

    public record ImageInput(
            @NotBlank @Size(max = 1000) String url,
            @NotNull ProductImage.Type type,
            Integer sortOrder
    ) {}
}
