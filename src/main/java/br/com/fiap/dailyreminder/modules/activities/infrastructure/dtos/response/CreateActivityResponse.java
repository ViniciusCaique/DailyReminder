package br.com.fiap.dailyreminder.modules.activities.infrastructure.dtos.response;

import br.com.fiap.dailyreminder.modules.notes.domain.Note;

import java.time.LocalDate;

public record CreateActivityResponse(
        String id,
        int duration,
        LocalDate dataDia,
        String name,
        Note note,
        String userId
) {

}
