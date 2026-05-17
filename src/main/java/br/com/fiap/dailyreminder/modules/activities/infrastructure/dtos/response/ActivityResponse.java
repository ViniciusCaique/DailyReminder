package br.com.fiap.dailyreminder.modules.activities.infrastructure.dtos.response;

import br.com.fiap.dailyreminder.modules.activities.domain.Activity;

import java.time.LocalDate;

public record ActivityResponse(
        String id,
        int duration,
        LocalDate dataDia,
        String name,
        String noteId,
        String userId
) {

    public static ActivityResponse from(Activity activity) {
        var note = activity.getLembrete();
        var user = activity.getUser();

        return new ActivityResponse(
                activity.getId().toString(),
                activity.getDuration(),
                activity.getDataDia(),
                activity.getName(),
                note != null ? note.getId().toString() : null,
                user != null ? user.getId().toString() : null
        );
    }
}
