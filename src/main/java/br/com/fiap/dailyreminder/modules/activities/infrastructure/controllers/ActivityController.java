package br.com.fiap.dailyreminder.modules.activities.infrastructure.controllers;

import java.util.List;
import java.util.UUID;

import br.com.fiap.dailyreminder.modules.activities.domain.Activity;
import br.com.fiap.dailyreminder.modules.activities.infrastructure.repositories.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
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
import br.com.fiap.dailyreminder.repository.NotesRepository;
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
@Tag(name = "Activities")
@RequestMapping("/api/activities")
public class ActivityController {

    @Autowired
    ActivityRepository activityRepository;

    @Autowired
    NotesRepository notesRepository;

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
    public List<Activity> index(){
        return activityRepository.findAll();
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
    public ResponseEntity<Object> create(@RequestBody @Valid Activity atividade){
        activityRepository.save(atividade);
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
    public EntityModel<Activity> show(@PathVariable UUID id) {
        var activity = activityRepository.findById(id).orElseThrow(() -> new RestNotFoundException("Atividade nao encontrada"));

        System.out.println(activity);
        return activity.toEntityModel();
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
    public EntityModel<Activity> update(@PathVariable UUID id, @Valid @RequestBody Activity activity) {
        activityRepository.findById(id).orElseThrow(() -> new RestNotFoundException("Erro ao alterar, atividade nao encontrada!"));
        activity.setId(id);
        activityRepository.save(activity);
        return activity.toEntityModel();
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
    public ResponseEntity<Activity> delete(@PathVariable UUID id) {
        var activity = activityRepository.findById(id).orElseThrow(() -> new RestNotFoundException("Erro ao apagar, atividade nao encontrada!"));
        activityRepository.delete(activity);
        return ResponseEntity.noContent().build();
    }
}
