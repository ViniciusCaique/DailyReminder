package br.com.fiap.dailyreminder.modules.activities.infrastructure.dtos.request;

import br.com.fiap.dailyreminder.modules.notes.domain.Note;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateActivityRequest(
        @Min(value = 0, message = "Nao existe tempo negativo!") Integer duration,
        LocalDate dataDia,
        @Size(min = 3, max = 255) String name,
        Note note
) {
}
