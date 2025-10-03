package br.com.fiap.dailyreminder.config;

import java.time.LocalDate;
import java.util.List;

import br.com.fiap.dailyreminder.modules.activities.infrastructure.repositories.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import br.com.fiap.dailyreminder.modules.activities.domain.Activity;
import br.com.fiap.dailyreminder.models.Lembrete;
import br.com.fiap.dailyreminder.models.Usuario;

import br.com.fiap.dailyreminder.repository.LembreteRepository;
import br.com.fiap.dailyreminder.repository.UsuarioRepository;

@Configuration
@Profile("dev")
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired
    LembreteRepository lembreteRepository;

    @Autowired
    ActivityRepository activityRepository;

    @Autowired
    UsuarioRepository usuarioRepository;


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

        activityRepository.saveAll(List.of(
            Activity.builder().duracao(60).dataDia(LocalDate.now()).name("estudar").lembrete(l1).build(),
            Activity.builder().duracao(30).dataDia(LocalDate.now()).name("treinar").lembrete(l3).build(),
            Activity.builder().duracao(50).dataDia(LocalDate.now()).name("ler").lembrete(l5).build(),
            Activity.builder().duracao(20).dataDia(LocalDate.now()).name("correr").build(),
            Activity.builder().duracao(10).dataDia(LocalDate.now()).name("passear com cachorro").build(),
            Activity.builder().duracao(120).dataDia(LocalDate.now()).name("ver serie").lembrete(l6).build(),
            Activity.builder().duracao(180).dataDia(LocalDate.now()).name("ver filme").build(),
            Activity.builder().duracao(150).dataDia(LocalDate.now()).name("jogar").lembrete(l7).build()
        ));

        usuarioRepository.save(Usuario.builder()
        .nome("Vinicius")
        .email("vinicius@gmail.com")
        .senha("$2a$12$xjbVbyBNGfeRN9dmHJ34/uwdhmc00XfYw3qY9sKSg4jEkB4Zwg72a")
        .build()
    );
    }
}
