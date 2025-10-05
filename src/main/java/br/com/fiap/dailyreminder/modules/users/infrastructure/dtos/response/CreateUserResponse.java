package br.com.fiap.dailyreminder.modules.users.infrastructure.dtos.response;

public record CreateUserResponse(
        String name,
        String email,
        String password
) {
}
