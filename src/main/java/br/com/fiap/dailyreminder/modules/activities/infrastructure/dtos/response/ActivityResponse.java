package br.com.fiap.dailyreminder.modules.activities.infrastructure.dtos.response;
import br.com.fiap.dailyreminder.modules.activities.domain.Activity;
import br.com.fiap.dailyreminder.modules.activities.domain.ActivityStatus;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
public record ActivityResponse(UUID id, Integer duration, LocalDate dataDia, String name, UUID noteId, UUID userId, boolean completed, Instant completedAt, String description, ActivityStatus status, LocalDate dueDate, UUID projectId, java.util.Set<UUID> labelIds, java.util.List<br.com.fiap.dailyreminder.modules.activities.domain.ChecklistItem> checklist, long version) {
    public static ActivityResponse from(Activity activity) {
        return new ActivityResponse(activity.getId(), activity.getDuration(), activity.getDataDia(), activity.getName(), activity.getNoteId(), activity.getUserId(), activity.isCompleted(), activity.getCompletedAt(), activity.getDescription(), activity.getStatus(), activity.getDueDate(), activity.getProjectId(), activity.getLabelIds(), activity.getChecklist(), activity.getVersion());
    }
}
