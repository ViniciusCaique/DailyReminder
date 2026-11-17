package br.com.fiap.dailyreminder.modules.notes.application;

import br.com.fiap.dailyreminder.exceptions.RestNotFoundException;
import br.com.fiap.dailyreminder.modules.notes.domain.*;
import br.com.fiap.dailyreminder.modules.notes.infrastructure.repositories.NotesRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReminderService {
    private final NotesRepository notes;
    private final ApplicationEventPublisher events;
    private final Clock clock;
    public ReminderService(NotesRepository notes, ApplicationEventPublisher events, Clock clock) { this.notes = notes; this.events = events; this.clock = clock; }
    public record Draft(String message, Instant scheduledAt, String timeZone, Recurrence recurrence, Set<Integer> repeatDays) {}
    @Transactional(readOnly = true) public List<Note> list(UUID userId) { return notes.findByUserIdOrderByScheduledAtAsc(userId); }
    @Transactional(readOnly = true) public Note owned(UUID id, UUID userId) { return notes.findByIdAndUserId(id, userId).orElseThrow(() -> new RestNotFoundException("Reminder not found")); }
    public Note save(UUID id, UUID userId, Draft draft) {
        var zone = draft.timeZone() == null ? "UTC" : draft.timeZone();
        ZoneId.of(zone);
        var recurrence = draft.recurrence() == null ? Recurrence.NONE : draft.recurrence();
        var days = draft.repeatDays() == null ? Set.<Integer>of() : draft.repeatDays();
        if (recurrence == Recurrence.CUSTOM && days.isEmpty()) throw new IllegalArgumentException("Choose at least one weekday");
        var note = id == null ? new Note() : owned(id, userId);
        note.setUserId(userId); note.setMessage(draft.message().trim()); note.setScheduledAt(draft.scheduledAt().truncatedTo(java.time.temporal.ChronoUnit.MICROS));
        note.setTimeZone(zone); note.setRecurrence(recurrence);
        note.setRepeatDays(days.stream().sorted().map(String::valueOf).collect(Collectors.joining(",")));
        return notes.save(note);
    }
    public Note complete(UUID id, UUID userId, boolean completed, Instant occurrence) {
        var note = owned(id, userId);
        if (completed && note.getRecurrence() != Recurrence.NONE && occurrence != null && !occurrence.equals(note.getScheduledAt())) return note;
        if (completed && !note.isCompleted()) {
            var now = Instant.now(clock); note.setLastCompletedAt(now);
            if (note.getRecurrence() != Recurrence.NONE) {
                note.setScheduledAt(ReminderSchedule.next(note.getScheduledAt(), now, note.getTimeZone(), note.getRecurrence(), days(note)));
                return notes.save(note);
            }
        }
        note.setCompleted(completed);
        return notes.save(note);
    }
    public void delete(UUID id, UUID userId) {
        var note = owned(id, userId);
        events.publishEvent(new ReminderDeleted(id));
        notes.delete(note);
    }
    public static Set<Integer> days(Note note) {
        if (note.getRepeatDays().isBlank()) return Set.of();
        return Arrays.stream(note.getRepeatDays().split(",")).map(Integer::valueOf).collect(Collectors.toSet());
    }
}
