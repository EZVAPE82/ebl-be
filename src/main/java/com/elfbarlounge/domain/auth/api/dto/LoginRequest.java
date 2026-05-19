package com.elfbarlounge.domain.auth.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @Email
        @NotBlank
        String email,

        @NotBlank
        @Size(max = 100)
        String password
) {
}
