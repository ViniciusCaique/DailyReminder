package br.com.fiap.dailyreminder.modules.activities.infrastructure.dtos.request;
import br.com.fiap.dailyreminder.modules.activities.domain.ActivityStatus;
import jakarta.validation.constraints.*;
import jakarta.validation.Valid;
import java.util.List;
import java.time.LocalDate;
import java.util.UUID;
public record UpdateActivityRequest(
    @Min(1) @Max(1440) Integer duration,
    LocalDate dataDia,
    @Size(min = 3, max = 255) @Pattern(regexp = ".*\\S.*", message = "Name must not be blank") String name,
    UUID noteId, Boolean clearReminder, Boolean completed,
    @Size(max = 10000) String description, ActivityStatus status, LocalDate dueDate,
    Boolean clearDuration, Boolean clearScheduledDate, Boolean clearDueDate,
    UUID projectId, Boolean clearProject, @Size(max = 20) List<@NotNull UUID> labelIds,
    @Size(max = 100) List<@NotNull @Valid ChecklistItemRequest> checklist, @PositiveOrZero Long version
) {}
