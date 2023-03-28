package br.com.fiap.dailyreminder.controllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import br.com.fiap.dailyreminder.repository.AtividadeRepository;

import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/atividades")
public class AtividadeController {
    
    Logger log = LoggerFactory.getLogger(AtividadeController.class);

    List<Atividade> atividades = new ArrayList<>();

    @Autowired
    AtividadeRepository repository;

    @GetMapping
    public List<Atividade> index(){
        return repository.findAll();
    }

    @PostMapping
    public ResponseEntity<Atividade> create(@RequestBody Atividade atividade){
        log.info("cadastrando atividade " + atividade);
        repository.save(atividade);
        return ResponseEntity.status(HttpStatus.CREATED).body(atividade);
    }

    @GetMapping("{id}")
    public ResponseEntity<Atividade> show(@PathVariable long id) {
        log.info("detalhando atividade " + id);
        // var atividadeEncontrada = atividades.stream().filter(a -> a.getId().equals(id)).findFirst();

        var atividadeEncontrada = repository.findById(id);

        if (atividadeEncontrada.isEmpty()){
            // return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(atividadeEncontrada.get());
    }

    @PutMapping("{id}")
    public ResponseEntity<Atividade> update(@PathVariable long id, @RequestBody Atividade atividade) {
        log.info("atualizando atividade " + id);
        // var atividadeEncontrada = atividades.stream().filter(a -> a.getId().equals(id)).findFirst();
        
        var atividadeEncontrada = repository.findById(id);

        if (atividadeEncontrada.isEmpty()){
            // return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            return ResponseEntity.notFound().build();
        }

        atividade.setId(id);
        repository.save(atividade);

        return ResponseEntity.ok(atividade);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Atividade> delete(@PathVariable long id) {
        log.info("apagando atividade " + id);
        // var atividadeEncontrada = atividades.stream().filter(a -> a.getId().equals(id)).findFirst();

        var atividadeEncontrada = repository.findById(id);

        if (atividadeEncontrada.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        repository.delete(atividadeEncontrada.get()); 

        // return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        return ResponseEntity.noContent().build();
    }
}
