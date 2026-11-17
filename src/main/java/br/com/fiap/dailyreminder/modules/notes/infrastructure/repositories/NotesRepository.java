package br.com.fiap.dailyreminder.modules.notes.infrastructure.repositories;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.jpa.repository.JpaRepository;
import br.com.fiap.dailyreminder.modules.notes.domain.Note;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotesRepository extends JpaRepository<Note, UUID> {
    List<Note> findByUserIdOrderByScheduledAtAsc(UUID userId);
    Optional<Note> findByIdAndUserId(UUID id, UUID userId);
}
