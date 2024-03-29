package br.com.fiap.dailyreminder.controllers;

import java.util.List;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.fiap.dailyreminder.exceptions.RestNotFoundException;
import br.com.fiap.dailyreminder.models.Atividade;
import br.com.fiap.dailyreminder.repository.AtividadeRepository;
import br.com.fiap.dailyreminder.repository.LembreteRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;


@RestController
@Slf4j
@SecurityRequirement(name = "bearer-key")
@Tag(name = "atividades")
@RequestMapping("/api/atividades")
public class AtividadeController {

    @Autowired
    AtividadeRepository atividadeRepository;

    @Autowired
    LembreteRepository lembreteRepository;

    @Autowired
    PagedResourcesAssembler<Object> assembler;

    // @GetMapping
    // @Operation(
    //     summary = "Detalhar atividades.",
    //     description = "Endpoint que retorna todas as atividades ou uma lista especifica baseado na busca." 
    // )
    // @ApiResponses({
    //     @ApiResponse(responseCode = "200", description = "atividade retornada com sucesso"),
    //     @ApiResponse(responseCode = "204", description = "sem conteudo"),
    //     @ApiResponse(responseCode = "400", description = "ma requisicao"),
    //     @ApiResponse(responseCode = "404", description = "atividade com id informado inexistente")
    // })
    // public PagedModel<EntityModel<Object>> index(@RequestParam(required = false) String busca, @ParameterObject @PageableDefault(size = 10) Pageable pageable){

    //     Page<Atividade> atividades = (busca == null) ? 
    //         atividadeRepository.findAll(pageable) : 
    //         atividadeRepository.findByAtividadeContaining(busca, pageable);
  
    //     return assembler.toModel(atividades.map(Atividade::toEntityModel));
    // }

    @GetMapping
    public List<Atividade> index(){
        return atividadeRepository.findAll();
    }

    @PostMapping
    @Operation(
        summary = "Cadastrar atividade.",
        description = "Endpoint que recebe os parametros de atividade e cria os dados de uma atividade." 
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "atividade cadastrada com sucesso"),
        @ApiResponse(responseCode = "400", description = "os campos enviados sao invalidos")
    })
    public ResponseEntity<Object> create(@RequestBody @Valid Atividade atividade){
        atividadeRepository.save(atividade);
        // atividade.setLembrete(lembreteRepository.findById(atividade.getLembrete().getId()).get());
        return ResponseEntity.status(HttpStatus.CREATED).body(atividade);
    }

    @GetMapping("{id}")
    @Operation(
        summary = "Detalhar atividade.",
        description = "Endpoint que recebe um id e retorna os dados de uma atividade." 
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "atividade retornada com sucesso"),
        @ApiResponse(responseCode = "204", description = "sem conteudo"),
        @ApiResponse(responseCode = "404", description = "atividade com id informado inexistente")
    })
    public EntityModel<Atividade> show(@PathVariable long id) {
        var atividade = atividadeRepository.findById(id).orElseThrow(() -> new RestNotFoundException("Atividade nao encontrada"));
        return atividade.toEntityModel();
    }

    @PutMapping("{id}")
    @Operation(
        summary = "Atualizar atividade.",
        description = "Endpoint que recebe os parametros de atividade e atualiza os dados de uma atividade." 
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "atividade atualizada com sucesso"),
        @ApiResponse(responseCode = "404", description = "nao existe atividade com o id informado"),
        @ApiResponse(responseCode = "406", description = "dado informado errado")
    })
    public EntityModel<Atividade> update(@PathVariable long id, @Valid @RequestBody Atividade atividade) {
        atividadeRepository.findById(id).orElseThrow(() -> new RestNotFoundException("Erro ao alterar, atividade nao encontrada!"));
        atividade.setId(id);
        atividadeRepository.save(atividade);
        return atividade.toEntityModel();
    }

    @DeleteMapping("{id}")
    @Operation(
        summary = "Deletar atividade.",
        description = "Endpoint que recebe um id e deleta uma atividade." 
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "sem conteudo/deletada com sucesso"),
        @ApiResponse(responseCode = "404", description = "nao existe atividade com o id informado")
    })
    public ResponseEntity<Atividade> delete(@PathVariable long id) {
        var atividade = atividadeRepository.findById(id).orElseThrow(() -> new RestNotFoundException("Erro ao apagar, atividade nao encontrada!"));
        atividadeRepository.delete(atividade); 
        return ResponseEntity.noContent().build();
    }
}
