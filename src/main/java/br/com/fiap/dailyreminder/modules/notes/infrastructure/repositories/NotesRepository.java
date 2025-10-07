package br.com.fiap.dailyreminder.modules.notes.infrastructure.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.fiap.dailyreminder.modules.notes.domain.Note;

import java.util.UUID;

public interface NotesRepository extends JpaRepository<Note, UUID> {

}
