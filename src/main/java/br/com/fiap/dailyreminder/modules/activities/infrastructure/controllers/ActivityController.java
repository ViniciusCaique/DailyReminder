package br.com.fiap.dailyreminder.modules.activities.infrastructure.controllers;

import br.com.fiap.dailyreminder.modules.activities.application.ActivityDetails;
import br.com.fiap.dailyreminder.modules.activities.application.usecases.ActivityUseCase;
import br.com.fiap.dailyreminder.modules.activities.infrastructure.dtos.request.CreateActivityRequest;
import br.com.fiap.dailyreminder.modules.activities.infrastructure.dtos.request.UpdateActivityRequest;
import br.com.fiap.dailyreminder.modules.activities.infrastructure.dtos.response.ActivityResponse;
import br.com.fiap.dailyreminder.shared.CurrentUser;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "Activities")
@SecurityRequirement(name = "bearer-key")
@RequestMapping("/api/activities")
public class ActivityController {
    private final ActivityUseCase activities;
    public ActivityController(ActivityUseCase activities) { this.activities = activities; }
    @GetMapping({"", "/", "/me"}) public List<ActivityResponse> index() { return activities.list(CurrentUser.id()).stream().map(ActivityResponse::from).toList(); }
    @GetMapping("/{id}") public ActivityResponse show(@PathVariable UUID id) { return ActivityResponse.from(activities.owned(id, CurrentUser.id())); }
    @PostMapping({"", "/"}) public ResponseEntity<ActivityResponse> create(@Valid @RequestBody CreateActivityRequest request) {
        return ResponseEntity.status(201).body(ActivityResponse.from(activities.create(CurrentUser.id(), request.name(), request.duration(), request.dataDia(), request.noteId(), request.description(), request.status(), request.dueDate(), new ActivityDetails(request.projectId(), false, request.labelIds(), request.checklist() == null ? null : request.checklist().stream().map(item -> item.change()).toList(), null))));
    }
    @PatchMapping("/{id}") public ActivityResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateActivityRequest request) {
        return ActivityResponse.from(activities.update(id, CurrentUser.id(), request.name(), request.duration(), request.dataDia(), request.noteId(), Boolean.TRUE.equals(request.clearReminder()), request.completed(), request.description(), request.status(), request.dueDate(), Boolean.TRUE.equals(request.clearDuration()), Boolean.TRUE.equals(request.clearScheduledDate()), Boolean.TRUE.equals(request.clearDueDate()), new ActivityDetails(request.projectId(), Boolean.TRUE.equals(request.clearProject()), request.labelIds(), request.checklist() == null ? null : request.checklist().stream().map(item -> item.change()).toList(), request.version())));
    }
    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable UUID id) { activities.delete(id, CurrentUser.id()); return ResponseEntity.noContent().build(); }
}
