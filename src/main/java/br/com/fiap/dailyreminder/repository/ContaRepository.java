package br.com.fiap.dailyreminder.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.fiap.dailyreminder.models.Conta;

public interface ContaRepository extends JpaRepository<Conta , Long>{
    
}
