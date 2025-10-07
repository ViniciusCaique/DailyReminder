package br.com.fiap.dailyreminder.modules.notes.infrastructure.controllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.fiap.dailyreminder.exceptions.RestNotFoundException;
import br.com.fiap.dailyreminder.modules.notes.domain.Note;
import br.com.fiap.dailyreminder.modules.notes.infrastructure.repositories.NotesRepository;
import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.RequestParam;


@RestController
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Notes")
@RequestMapping("/api/notes")
public class NoteController {

    @Autowired
    NotesRepository notesRepository;

    @GetMapping
    @Operation(
        summary = "Detalhar notas.",
        description = "Endpoint que retorna todos os notas."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Nota retornado com sucesso"),
        @ApiResponse(responseCode = "204", description = "sem conteudo"),
        @ApiResponse(responseCode = "400", description = "ma requisicao"),
        @ApiResponse(responseCode = "404", description = "Nota com id informado inexistente")
    })
    public Page<Note> index(@RequestParam(required = false) String busca, @PageableDefault(size = 5) Pageable pageable){
        return notesRepository.findAll(pageable);
    }

    @PostMapping
    @Operation(
        summary = "Cadastrar nota.",
        description = "Endpoint que recebe os parametros de nota e cria os dados de um nota."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Nota cadastrado com sucesso"),
        @ApiResponse(responseCode = "400", description = "os campos enviados sao invalidos")
    })
    public ResponseEntity<Object> create(@RequestBody @Valid Note lembrete){

        notesRepository.save(lembrete);
        return ResponseEntity.status(HttpStatus.CREATED).body(lembrete);
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Detalhar nota.",
        description = "Endpoint que recebe um id e retorna os dados de um nota."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Nota retornado com sucesso"),
        @ApiResponse(responseCode = "204", description = "sem conteudo"),
        @ApiResponse(responseCode = "404", description = "Nota com id informado inexistente")
    })
    public ResponseEntity<Note> show(@PathVariable UUID id) {
        var lembrete = notesRepository.findById(id).orElseThrow(() -> new RestNotFoundException("Nota nao encontrada"));
        return ResponseEntity.ok(lembrete);
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Atualizar nota.",
        description = "Endpoint que recebe os parametros de um nota e atualiza os dados de um nota."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Nota atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "nao existe nota com o id informado"),
        @ApiResponse(responseCode = "406", description = "dado informado errado")
    })
    public ResponseEntity<Note> update(@PathVariable UUID id, @Valid @RequestBody Note lembrete) {

        notesRepository.findById(id).orElseThrow(() -> new RestNotFoundException("Erro ao alterar, nota nao encontrada!"));
        lembrete.setId(id);
        notesRepository.save(lembrete);
        return ResponseEntity.ok(lembrete);
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Deletar Lembrete.",
        description = "Endpoint que recebe um id e deleta um lembrete." 
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "sem conteudo/deletado com sucesso"),
        @ApiResponse(responseCode = "404", description = "nao existe lembrete com o id informado")
    })
    public ResponseEntity<Note> delete(@PathVariable UUID id) {

        var lembrete = notesRepository.findById(id).orElseThrow(() -> new RestNotFoundException("Erro ao apagar, lembrete nao encontrada!"));
        notesRepository.delete(lembrete);
        return ResponseEntity.noContent().build();
    }
}
