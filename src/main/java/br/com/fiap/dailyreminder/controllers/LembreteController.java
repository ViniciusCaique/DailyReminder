package br.com.fiap.dailyreminder.controllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

    @Autowired
    LembreteRepository lembreteRepository;

    @GetMapping
    public Page<Lembrete> index(@RequestParam(required = false) String busca, @PageableDefault(size = 5) Pageable pageable){
        return lembreteRepository.findAll(pageable);
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody @Valid Lembrete lembrete){

        lembreteRepository.save(lembrete);
        return ResponseEntity.status(HttpStatus.CREATED).body(lembrete);
    }

    @GetMapping("{id}")
    public ResponseEntity<Lembrete> show(@PathVariable long id) {
        var lembrete = lembreteRepository.findById(id).orElseThrow(() -> new RestNotFoundException("Lembrete nao encontrada"));
        return ResponseEntity.ok(lembrete);
    }

    @PutMapping("{id}")
    public ResponseEntity<Lembrete> update(@PathVariable long id, @Valid @RequestBody Lembrete lembrete) {

        lembreteRepository.findById(id).orElseThrow(() -> new RestNotFoundException("Erro ao alterar, lembrete nao encontrada!"));
        lembrete.setId(id);
        lembreteRepository.save(lembrete);
        return ResponseEntity.ok(lembrete);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Lembrete> delete(@PathVariable long id) {

        var lembrete = lembreteRepository.findById(id).orElseThrow(() -> new RestNotFoundException("Erro ao apagar, lembrete nao encontrada!"));
        lembreteRepository.delete(lembrete); 
        return ResponseEntity.noContent().build();
    }
}
