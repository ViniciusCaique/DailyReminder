package br.com.fiap.dailyreminder.controllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.fiap.dailyreminder.models.Atividade;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class AtividadeController {
    
    Logger log = LoggerFactory.getLogger(AtividadeController.class);

    List<Atividade> atividades = new ArrayList<>();

    @GetMapping("api/atividades")
    public List<Atividade> index(){
        return atividades;
    }

    @PostMapping("api/atividades")
    public void create(@RequestBody Atividade atividade){
        log.info("cadastrando atividade " + atividade);
        atividade.setId(atividades.size() + 1l);
        atividades.add(atividade);
    }

    @GetMapping("api/atividades/{id}")
    public ResponseEntity<Atividade> show(@PathVariable long id) {
        log.info("detalhando atividade " + id);
        var atividadeEncontrada = atividades.stream().filter(a -> a.getId().equals(id)).findFirst();

        if (atividadeEncontrada.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(atividadeEncontrada.get());
    }

    @PutMapping("api/atividades/{id}")
    public ResponseEntity<Atividade> update(@PathVariable long id, @RequestBody Atividade atividade) {
        log.info("atualizando atividade " + id);
        var atividadeEncontrada = atividades.stream().filter(a -> a.getId().equals(id)).findFirst();

        if (atividadeEncontrada.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        atividades.remove(atividadeEncontrada.get());
        atividade.setId(id);
        atividades.add(atividade);

        return ResponseEntity.ok(atividade);
    }

    @DeleteMapping("api/atividades/{id}")
    public ResponseEntity<Atividade> delete(@PathVariable long id) {
        log.info("apagando atividade " + id);
        var atividadeEncontrada = atividades.stream().filter(a -> a.getId().equals(id)).findFirst();

        if (atividadeEncontrada.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        atividades.remove(atividadeEncontrada.get()); 

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
