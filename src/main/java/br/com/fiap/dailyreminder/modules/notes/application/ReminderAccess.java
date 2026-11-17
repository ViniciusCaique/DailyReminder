package br.com.fiap.dailyreminder.modules.notes.application;

import br.com.fiap.dailyreminder.modules.notes.infrastructure.repositories.NotesRepository;
import br.com.fiap.dailyreminder.exceptions.RestNotFoundException;
import org.springframework.stereotype.Service;
import java.util.UUID;

/** Public module API for consumers; persistence details stay within the reminders module. */
@Service
public class ReminderAccess {
    private final NotesRepository notes;
    public ReminderAccess(NotesRepository notes) { this.notes = notes; }
    public void requireOwned(UUID id, UUID userId) {
        notes.findByIdAndUserId(id, userId).orElseThrow(() -> new RestNotFoundException("Reminder not found"));
    }
}
