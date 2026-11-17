package br.com.fiap.dailyreminder.modules.activities.application;
import java.util.List;
import java.util.UUID;
public record ActivityDetails(UUID projectId, boolean clearProject, List<UUID> labelIds, List<ChecklistChange> checklist, Long version) {
    public record ChecklistChange(UUID id, String text, boolean completed) {}
}
