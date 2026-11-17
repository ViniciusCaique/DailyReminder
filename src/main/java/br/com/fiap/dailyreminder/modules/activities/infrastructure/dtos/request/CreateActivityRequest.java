package br.com.fiap.dailyreminder.modules.activities.infrastructure.dtos.request;
import br.com.fiap.dailyreminder.modules.activities.domain.ActivityStatus;
import jakarta.validation.constraints.*;
import jakarta.validation.Valid;
import java.util.List;
import java.time.LocalDate;
import java.util.UUID;
public record CreateActivityRequest(
    @Min(1) @Max(1440) Integer duration,
    LocalDate dataDia,
    @NotBlank @Size(min = 3, max = 255) String name,
    UUID noteId,
    @Size(max = 10000) String description, ActivityStatus status, LocalDate dueDate, UUID projectId,
    @Size(max = 20) List<@NotNull UUID> labelIds, @Size(max = 100) List<@NotNull @Valid ChecklistItemRequest> checklist
) {
    public CreateActivityRequest(Integer duration, LocalDate dataDia, String name, UUID noteId) {
        this(duration, dataDia, name, noteId, null, null, null, null, null, null);
    }
}
