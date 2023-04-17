package br.com.fiap.dailyreminder.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import br.com.fiap.dailyreminder.models.Atividade;

public interface AtividadeRepository extends JpaRepository<Atividade, Long> {

    Page<Atividade> findByAtividadeContaining(String busca, Pageable pageable);
    
}
