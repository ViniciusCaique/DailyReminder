package br.com.fiap.dailyreminder.modules.activities.infrastructure.dtos.request;

import br.com.fiap.dailyreminder.modules.notes.domain.Note;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.util.Date;

public record CreateActivityRequest(
        @NotNull @Min(value = 0, message = "Nao existe tempo negativo!") int duration,
        @NotNull LocalDate dataDia,
        @NotBlank @Size(min = 3, max = 255) String name,
        Note note
) {
}
