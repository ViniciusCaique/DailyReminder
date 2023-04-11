package br.com.fiap.dailyreminder.config;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import br.com.fiap.dailyreminder.models.Lembrete;
import br.com.fiap.dailyreminder.repository.LembreteRepository;

@Configuration
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired
    LembreteRepository lembreteRepository;


    @Override
    public void run(String... args) throws Exception {
        lembreteRepository.saveAll(List.of(
            new Lembrete(1L, "Estudar java", 60),
            new Lembrete(2L, "Estudar javascript", 60),
            new Lembrete(3L, "Treinar costas", 60)
            ));
        // atividade repository
    }
    
}
