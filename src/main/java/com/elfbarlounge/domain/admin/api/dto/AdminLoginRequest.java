package com.elfbarlounge.domain.admin.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AdminLoginRequest(
        @NotBlank @Size(max = 80) String username,
        @NotBlank @Size(max = 100) String password
) {
}
