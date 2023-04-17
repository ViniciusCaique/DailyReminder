package br.com.fiap.dailyreminder.config;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import br.com.fiap.dailyreminder.models.Atividade;
import br.com.fiap.dailyreminder.models.Lembrete;
import br.com.fiap.dailyreminder.repository.AtividadeRepository;
import br.com.fiap.dailyreminder.repository.LembreteRepository;

@Configuration
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired
    LembreteRepository lembreteRepository;

    @Autowired
    AtividadeRepository atividadeRepository;


    @Override
    public void run(String... args) throws Exception {
        Lembrete l1 = new Lembrete(1L, "Estudar java", 60);
        Lembrete l2 = new Lembrete(2L, "Estudar javascript", 60);
        Lembrete l3 = new Lembrete(3L, "Treinar costas", 60);
        Lembrete l4 = new Lembrete(4L, "Treinar perna", 60);
        Lembrete l5 = new Lembrete(5L, "Ler Harry Potter", 60);
        Lembrete l6 = new Lembrete(6L, "Serie The Last of Us", 60);
        Lembrete l7 = new Lembrete(7L, "Jogar Valorant", 60);
        lembreteRepository.saveAll(List.of(
            l1, l2, l3, l4, l5, l6, l7
        ));

        atividadeRepository.saveAll(List.of(
            Atividade.builder().duracao(60).data(LocalDate.now()).atividade("estudar").lembrete(l1).build(),
            Atividade.builder().duracao(30).data(LocalDate.now()).atividade("treinar").lembrete(l3).build(),
            Atividade.builder().duracao(50).data(LocalDate.now()).atividade("ler").lembrete(l5).build(),
            Atividade.builder().duracao(20).data(LocalDate.now()).atividade("correr").build(),
            Atividade.builder().duracao(10).data(LocalDate.now()).atividade("passear com cachorro").build(),
            Atividade.builder().duracao(120).data(LocalDate.now()).atividade("ver serie").lembrete(l6).build(),
            Atividade.builder().duracao(180).data(LocalDate.now()).atividade("ver filme").build(),
            Atividade.builder().duracao(150).data(LocalDate.now()).atividade("jogar").lembrete(l7).build()
        )); 
    }
}
