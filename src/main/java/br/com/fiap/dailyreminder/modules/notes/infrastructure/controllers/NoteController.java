package br.com.fiap.dailyreminder.modules.notes.infrastructure.controllers;

import br.com.fiap.dailyreminder.modules.notes.application.ReminderService;
import br.com.fiap.dailyreminder.modules.notes.domain.Note;
import br.com.fiap.dailyreminder.modules.notes.domain.Recurrence;
import br.com.fiap.dailyreminder.shared.CurrentUser;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.*;

@RestController
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Reminders")
@RequestMapping("/api/notes")
public class NoteController {
    private final ReminderService reminders;
    public NoteController(ReminderService reminders) { this.reminders = reminders; }
    public record ReminderRequest(@NotBlank @Size(max = 255) String message, @NotNull Instant scheduledAt, @Size(max = 80) String timeZone, Recurrence recurrence, Set<@NotNull @Min(1) @Max(7) Integer> repeatDays) {
        ReminderService.Draft draft() { return new ReminderService.Draft(message, scheduledAt, timeZone, recurrence, repeatDays); }
    }
    public record CompletionRequest(@NotNull Boolean completed, Instant occurrence) {}
    public record ReminderResponse(UUID id, String message, Instant scheduledAt, boolean completed, String timeZone, Recurrence recurrence, Set<Integer> repeatDays, Instant lastCompletedAt) {
        static ReminderResponse from(Note note) { return new ReminderResponse(note.getId(), note.getMessage(), note.getScheduledAt(), note.isCompleted(), note.getTimeZone(), note.getRecurrence(), ReminderService.days(note), note.getLastCompletedAt()); }
    }
    @GetMapping public List<ReminderResponse> index() { return reminders.list(CurrentUser.id()).stream().map(ReminderResponse::from).toList(); }
    @PostMapping public ResponseEntity<ReminderResponse> create(@Valid @RequestBody ReminderRequest request) { return ResponseEntity.status(201).body(ReminderResponse.from(reminders.save(null, CurrentUser.id(), request.draft()))); }
    @GetMapping("/{id}") public ReminderResponse show(@PathVariable UUID id) { return ReminderResponse.from(reminders.owned(id, CurrentUser.id())); }
    @PutMapping("/{id}") public ReminderResponse update(@PathVariable UUID id, @Valid @RequestBody ReminderRequest request) { return ReminderResponse.from(reminders.save(id, CurrentUser.id(), request.draft())); }
    @PatchMapping("/{id}") public ReminderResponse complete(@PathVariable UUID id, @Valid @RequestBody CompletionRequest request) { return ReminderResponse.from(reminders.complete(id, CurrentUser.id(), request.completed(), request.occurrence())); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable UUID id) { reminders.delete(id, CurrentUser.id()); return ResponseEntity.noContent().build(); }
}
