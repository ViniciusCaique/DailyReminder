package br.com.fiap.dailyreminder.modules.users.infrastructure.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
        @NotNull String name,
        @NotNull @Email String email,
        @NotNull @Size(min = 6) String password
) {
}
