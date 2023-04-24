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
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
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
import br.com.fiap.dailyreminder.models.RestValidationError;
import br.com.fiap.dailyreminder.repository.AtividadeRepository;
import br.com.fiap.dailyreminder.repository.LembreteRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.RequestParam;


@RestController
@Slf4j
@RequestMapping("/api/atividades")
public class AtividadeController {

    @Autowired
    AtividadeRepository atividadeRepository;

    @Autowired
    LembreteRepository lembreteRepository;

    @Autowired
    PagedResourcesAssembler<Object> assembler;

    @GetMapping
    public PagedModel<EntityModel<Object>> index(@RequestParam(required = false) String busca, @PageableDefault(size = 10) Pageable pageable){

        Page<Atividade> atividades = (busca == null) ? 
            atividadeRepository.findAll(pageable) : 
            atividadeRepository.findByAtividadeContaining(busca, pageable);
            
        return assembler.toModel(atividades.map(Atividade::toEntityModel));
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody @Valid Atividade atividade){
        atividadeRepository.save(atividade);
        atividade.setLembrete(lembreteRepository.findById(atividade.getLembrete().getId()).get());
        return ResponseEntity.status(HttpStatus.CREATED).body(atividade);
    }

    @GetMapping("{id}")
    public EntityModel<Atividade> show(@PathVariable long id) {
        var atividade = atividadeRepository.findById(id).orElseThrow(() -> new RestNotFoundException("Atividade nao encontrada"));
        return atividade.toEntityModel();
    }

    @PutMapping("{id}")
    public EntityModel<Atividade> update(@PathVariable long id, @Valid @RequestBody Atividade atividade) {
        atividadeRepository.findById(id).orElseThrow(() -> new RestNotFoundException("Erro ao alterar, atividade nao encontrada!"));
        atividade.setId(id);
        atividadeRepository.save(atividade);
        return atividade.toEntityModel();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Atividade> delete(@PathVariable long id) {
        var atividade = atividadeRepository.findById(id).orElseThrow(() -> new RestNotFoundException("Erro ao apagar, atividade nao encontrada!"));
        atividadeRepository.delete(atividade); 
        return ResponseEntity.noContent().build();
    }
}
