package br.com.fiap.dailyreminder.modules.users.infrastructure.dtos.request;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
        @NotBlank String refreshToken
) {
}
