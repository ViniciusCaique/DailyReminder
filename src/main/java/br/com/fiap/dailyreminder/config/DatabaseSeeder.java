package br.com.fiap.dailyreminder.config;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import br.com.fiap.dailyreminder.models.Conta;
import br.com.fiap.dailyreminder.repository.ContaRepository;

@Configuration
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired
    ContaRepository contaRepository;


    @Override
    public void run(String... args) throws Exception {
        contaRepository.saveAll(List.of(
            new Conta(1L, "itau", new BigDecimal(100), "money", true),
            new Conta(2L, "banco do brasil", new BigDecimal(55), "money", true),
            new Conta(3L, "bradesco", new BigDecimal(150), "coin", true)
            ));
        // atividade repository
    }
    
}
