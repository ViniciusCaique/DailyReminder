package br.com.fiap.dailyreminder.modules.activities.infrastructure.dtos.request;
import br.com.fiap.dailyreminder.modules.activities.application.ActivityDetails;
import jakarta.validation.constraints.*;
import java.util.UUID;
public record ChecklistItemRequest(UUID id, @NotBlank @Size(max = 255) String text, boolean completed) {
    public ActivityDetails.ChecklistChange change() { return new ActivityDetails.ChecklistChange(id, text, completed); }
}
