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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.fiap.dailyreminder.exceptions.RestNotFoundException;
import br.com.fiap.dailyreminder.models.Lembrete;
import br.com.fiap.dailyreminder.models.RestValidationError;
import br.com.fiap.dailyreminder.repository.LembreteRepository;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/lembretes")
public class LembreteController {
    
    Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    LembreteRepository repository;

    @GetMapping
    public List<Lembrete> index(){
        return repository.findAll();
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody @Valid Lembrete lembrete){
        log.info("cadastrando lembrete " + lembrete);
        repository.save(lembrete);
        return ResponseEntity.status(HttpStatus.CREATED).body(lembrete);
    }

    @GetMapping("{id}")
    public ResponseEntity<Lembrete> show(@PathVariable long id) {
        log.info("detalhando lembrete " + id);
        // var atividadeEncontrada = atividades.stream().filter(a -> a.getId().equals(id)).findFirst();

        var lembrete = getLembrete(id);

        // if (atividadeEncontrada.isEmpty()){
        //     // return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        //     return ResponseEntity.notFound().build();
        // }
        return ResponseEntity.ok(lembrete);
    }

    @PutMapping("{id}")
    public ResponseEntity<Lembrete> update(@PathVariable long id, @Valid @RequestBody Lembrete lembrete) {
        log.info("atualizando lembrete " + id);
        // var atividadeEncontrada = atividades.stream().filter(a -> a.getId().equals(id)).findFirst();
        
        // var atividadeEncontrada = repository.findById(id);

        // if (atividadeEncontrada.isEmpty()){
        //     // return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        //     return ResponseEntity.notFound().build();
        // }

        getLembrete(id);

        lembrete.setId(id);
        repository.save(lembrete);

        return ResponseEntity.ok(lembrete);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Lembrete> delete(@PathVariable long id) {
        log.info("apagando lembrete " + id);
        // var atividadeEncontrada = atividades.stream().filter(a -> a.getId().equals(id)).findFirst();

        // var atividadeEncontrada = repository.findById(id);

        // if (atividadeEncontrada.isEmpty()){
        //     return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        // }

        // var atividade = getConta(id);
        //  da pra fazer assim e passar o nome da variavel no delete()
        var lembrete = getLembrete(id);
        repository.save(lembrete);
        repository.delete(getLembrete(id)); 

        // return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        return ResponseEntity.noContent().build();
    }

    private Lembrete getLembrete(long id) {
        return repository.findById(id).orElseThrow(() -> new RestNotFoundException("Conta nao encontrada"));
    }
}
