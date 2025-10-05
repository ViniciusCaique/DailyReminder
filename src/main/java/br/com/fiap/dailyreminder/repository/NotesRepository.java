package br.com.fiap.dailyreminder.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.fiap.dailyreminder.models.Lembrete;

import java.util.UUID;

public interface NotesRepository extends JpaRepository<Lembrete, UUID> {

}
