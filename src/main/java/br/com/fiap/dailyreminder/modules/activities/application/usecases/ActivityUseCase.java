package br.com.fiap.dailyreminder.modules.activities.application.usecases;

import br.com.fiap.dailyreminder.modules.activities.application.*;
import br.com.fiap.dailyreminder.modules.activities.domain.ChecklistItem;
import br.com.fiap.dailyreminder.modules.activities.domain.Activity;
import br.com.fiap.dailyreminder.modules.activities.domain.ActivityStatus;
import br.com.fiap.dailyreminder.modules.activities.infrastructure.repositories.ActivityRepository;
import br.com.fiap.dailyreminder.modules.notes.application.ReminderAccess;
import br.com.fiap.dailyreminder.modules.notes.application.ReminderDeleted;
import br.com.fiap.dailyreminder.exceptions.RestNotFoundException;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ActivityUseCase {
    private final ActivityRepository activities;
    private final ReminderAccess reminders;
    private final Clock clock;
    private final OrganizationService organization;
    public ActivityUseCase(ActivityRepository activities, ReminderAccess reminders, Clock clock, OrganizationService organization) {
        this.activities = activities; this.reminders = reminders; this.clock = clock; this.organization = organization;
    }
    @Transactional(readOnly = true)
    public List<Activity> list(UUID userId) { return activities.findByUserIdOrderByDataDiaAsc(userId); }
    @Transactional(readOnly = true)
    public Activity owned(UUID id, UUID userId) {
        return activities.findByIdAndUserId(id, userId).orElseThrow(() -> new RestNotFoundException("Activity not found"));
    }
    public Activity create(UUID userId, String name, Integer duration, LocalDate date, UUID noteId) {
        return create(userId, name, duration, date, noteId, null, null, null);
    }
    public Activity create(UUID userId, String name, Integer duration, LocalDate date, UUID noteId, String description, ActivityStatus status, LocalDate dueDate) {
        if (noteId != null) reminders.requireOwned(noteId, userId);
        var activity = new Activity();
        activity.setUserId(userId); activity.setName(name.trim()); activity.setDuration(duration);
        activity.setDataDia(date); activity.setNoteId(noteId);
        activity.setDescription(description == null ? null : description.trim()); activity.setDueDate(dueDate);
        activity.setStatus(status == null ? ActivityStatus.INBOX : status);
        if (activity.isCompleted()) activity.setCompletedAt(Instant.now(clock));
        return activities.save(activity);
    }
    public Activity update(UUID id, UUID userId, String name, Integer duration, LocalDate date, UUID noteId, boolean clearReminder, Boolean completed) {
        return update(id, userId, name, duration, date, noteId, clearReminder, completed, null, null, null, false, false, false);
    }
    public Activity update(UUID id, UUID userId, String name, Integer duration, LocalDate date, UUID noteId, boolean clearReminder, Boolean completed,
                           String description, ActivityStatus status, LocalDate dueDate, boolean clearDuration, boolean clearScheduledDate, boolean clearDueDate) {
        var activity = owned(id, userId);
        if (completed != null && status != null && completed != (status == ActivityStatus.DONE))
            throw new IllegalArgumentException("Status and completion must agree");
        if (description != null) activity.setDescription(description.trim());
        if (clearDuration) activity.setDuration(null);
        if (clearScheduledDate) activity.setDataDia(null);
        if (clearDueDate) activity.setDueDate(null);
        else if (dueDate != null) activity.setDueDate(dueDate);
        if (name != null) activity.setName(name.trim());
        if (!clearDuration && duration != null) activity.setDuration(duration);
        if (!clearScheduledDate && date != null) activity.setDataDia(date);
        if (clearReminder) activity.setNoteId(null);
        else if (noteId != null) { reminders.requireOwned(noteId, userId); activity.setNoteId(noteId); }
        ActivityStatus next = status != null ? status : completed == null ? activity.getStatus() : completed ? ActivityStatus.DONE : ActivityStatus.TODO;
        if (next != activity.getStatus()) {
            activity.setStatus(next); activity.setCompletedAt(next == ActivityStatus.DONE ? Instant.now(clock) : null);
        }
        return activities.save(activity);
    }
    public Activity create(UUID userId, String name, Integer duration, LocalDate date, UUID noteId, String description, ActivityStatus status, LocalDate dueDate, ActivityDetails details) {
        var activity = create(userId, name, duration, date, noteId, description, status, dueDate);
        applyDetails(activity, userId, details); return activities.save(activity);
    }
    public Activity update(UUID id, UUID userId, String name, Integer duration, LocalDate date, UUID noteId, boolean clearReminder, Boolean completed,
                           String description, ActivityStatus status, LocalDate dueDate, boolean clearDuration, boolean clearScheduledDate, boolean clearDueDate, ActivityDetails details) {
        var current = owned(id, userId);
        if (details.version() != null && details.version() != current.getVersion()) throw new ActivityConflict("This activity changed. Reload it before saving your edits.");
        var activity = update(id, userId, name, duration, date, noteId, clearReminder, completed, description, status, dueDate, clearDuration, clearScheduledDate, clearDueDate);
        applyDetails(activity, userId, details); return activities.save(activity);
    }
    private void applyDetails(Activity activity, UUID userId, ActivityDetails details) {
        if (details.clearProject()) activity.setProjectId(null);
        else if (details.projectId() != null) { organization.requireProject(details.projectId(), userId); activity.setProjectId(details.projectId()); }
        if (details.labelIds() != null) {
            if (details.labelIds().size() > 20 || new java.util.HashSet<>(details.labelIds()).size() != details.labelIds().size()) throw new IllegalArgumentException("Choose at most 20 different labels");
            details.labelIds().forEach(id -> organization.requireLabel(id, userId));
            activity.getLabelIds().clear(); activity.getLabelIds().addAll(details.labelIds());
        }
        if (details.checklist() != null) {
            if (details.checklist().size() > 100) throw new IllegalArgumentException("Use at most 100 checklist items");
            var existing = activity.getChecklist().stream().map(ChecklistItem::getId).collect(java.util.stream.Collectors.toSet());
            var seen = new java.util.HashSet<UUID>(); var next = new java.util.ArrayList<ChecklistItem>();
            for (var change : details.checklist()) {
                UUID itemId = change.id() == null ? UUID.randomUUID() : change.id();
                if (change.id() != null && !existing.contains(itemId)) throw new RestNotFoundException("Checklist item not found");
                if (!seen.add(itemId)) throw new IllegalArgumentException("Checklist items must be unique");
                String text = change.text() == null ? "" : change.text().trim();
                if (text.isEmpty() || text.length() > 255) throw new IllegalArgumentException("Checklist text must contain 1 to 255 characters");
                next.add(new ChecklistItem(itemId, text, change.completed()));
            }
            activity.getChecklist().clear(); activity.getChecklist().addAll(next);
        }
    }
    public void delete(UUID id, UUID userId) { activities.delete(owned(id, userId)); }
    @EventListener
    public void reminderDeleted(ReminderDeleted event) { activities.detachReminder(event.id()); }
}
