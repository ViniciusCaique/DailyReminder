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
import br.com.fiap.dailyreminder.models.Atividade;
import br.com.fiap.dailyreminder.models.Conta;
import br.com.fiap.dailyreminder.models.RestValidationError;
import br.com.fiap.dailyreminder.repository.ContaRepository;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/contas")
public class ContaController {
    
    Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    ContaRepository repository;

    @GetMapping
    public List<Conta> index(){
        return repository.findAll();
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody @Valid Conta conta){
        log.info("cadastrando conta " + conta);
        repository.save(conta);
        return ResponseEntity.status(HttpStatus.CREATED).body(conta);
    }

    @GetMapping("{id}")
    public ResponseEntity<Conta> show(@PathVariable long id) {
        log.info("detalhando atividade " + id);
        // var atividadeEncontrada = atividades.stream().filter(a -> a.getId().equals(id)).findFirst();

        var atividade = getConta(id);

        // if (atividadeEncontrada.isEmpty()){
        //     // return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        //     return ResponseEntity.notFound().build();
        // }
        return ResponseEntity.ok(atividade);
    }

    @PutMapping("{id}")
    public ResponseEntity<Conta> update(@PathVariable long id, @Valid @RequestBody Conta conta) {
        log.info("atualizando atividade " + id);
        // var atividadeEncontrada = atividades.stream().filter(a -> a.getId().equals(id)).findFirst();
        
        // var atividadeEncontrada = repository.findById(id);

        // if (atividadeEncontrada.isEmpty()){
        //     // return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        //     return ResponseEntity.notFound().build();
        // }

        getConta(id);

        conta.setId(id);
        repository.save(conta);

        return ResponseEntity.ok(conta);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Conta> delete(@PathVariable long id) {
        log.info("apagando conta " + id);
        // var atividadeEncontrada = atividades.stream().filter(a -> a.getId().equals(id)).findFirst();

        // var atividadeEncontrada = repository.findById(id);

        // if (atividadeEncontrada.isEmpty()){
        //     return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        // }

        // var atividade = getConta(id);
        //  da pra fazer assim e passar o nome da variavel no delete()
        var conta = getConta(id);
        conta.setAtiva(false);
        repository.save(conta);
        repository.delete(getConta(id)); 

        // return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        return ResponseEntity.noContent().build();
    }

    private Conta getConta(long id) {
        return repository.findById(id).orElseThrow(() -> new RestNotFoundException("Conta nao encontrada"));
    }
}
