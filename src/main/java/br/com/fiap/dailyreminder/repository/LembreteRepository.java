package br.com.fiap.dailyreminder.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.fiap.dailyreminder.models.Lembrete;

public interface LembreteRepository extends JpaRepository<Lembrete, Long> {
    
}
