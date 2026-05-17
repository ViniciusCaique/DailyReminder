package br.com.fiap.dailyreminder.modules.users.infrastructure.dtos.response;

public record RefreshTokenResponse(
        String token,
        String refreshToken
) {
}
