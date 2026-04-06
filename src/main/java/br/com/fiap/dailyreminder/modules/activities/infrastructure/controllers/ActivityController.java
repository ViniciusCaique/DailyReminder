package br.com.fiap.dailyreminder.modules.activities.infrastructure.controllers;

import java.util.List;
import java.util.UUID;

import br.com.fiap.dailyreminder.modules.activities.application.usecases.ActivityUseCase;
import br.com.fiap.dailyreminder.modules.activities.domain.Activity;
import br.com.fiap.dailyreminder.modules.activities.infrastructure.dtos.request.CreateActivityRequest;
import br.com.fiap.dailyreminder.modules.activities.infrastructure.dtos.request.UpdateActivityRequest;
import br.com.fiap.dailyreminder.modules.activities.infrastructure.dtos.response.CreateActivityResponse;
import br.com.fiap.dailyreminder.modules.activities.infrastructure.repositories.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import br.com.fiap.dailyreminder.exceptions.RestNotFoundException;
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
@RequestMapping("/api/activities/")
public class ActivityController {

    private final ActivityRepository activityRepository;
    private final ActivityUseCase activityUseCase;

    public ActivityController(
            ActivityUseCase activityUseCase,
            ActivityRepository activityRepository
    ) {
      this.activityUseCase = activityUseCase;
      this.activityRepository = activityRepository;
    }

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
    @Operation(
            summary = "Retorna todas as atividades de todos os usuários.",
            description = "Endpoint que retorna todas as atividades de todos os usuários."
    )
    public List<Activity> index(){
        return activityUseCase.findAll();
    }

    @GetMapping("/me")
    @Operation(
            summary = "Retorna todas as atividades de um usuário.",
            description = "Endpoint que retorna todas as atividades de um usuário."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "atividade retornada com sucesso"),
            @ApiResponse(responseCode = "204", description = "sem conteudo"),
    })
    public List<Activity> me() {
      String userId = SecurityContextHolder.getContext()
              .getAuthentication()
              .getPrincipal()
              .toString();


      List<Activity> activities = activityUseCase.findAllUserActivies(userId);

      return activities;
    }

    @GetMapping("/{id}")
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

      return activity.toEntityModel();
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
    public ResponseEntity<CreateActivityResponse> create(@RequestBody @Valid CreateActivityRequest createActivityRequest){
        String userId = SecurityContextHolder.getContext()
              .getAuthentication()
              .getPrincipal()
              .toString();

        var response = activityUseCase.create(userId, createActivityRequest);

        // atividade.setLembrete(lembreteRepository.findById(atividade.getLembrete().getId()).get());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @PatchMapping("/{id}")
    @Operation(
        summary = "Atualizar atividade.",
        description = "Endpoint que recebe os parametros de atividade e atualiza os dados de uma atividade." 
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "atividade atualizada com sucesso"),
        @ApiResponse(responseCode = "404", description = "nao existe atividade com o id informado"),
        @ApiResponse(responseCode = "406", description = "dado informado errado")
    })
    public EntityModel<Activity> update(@PathVariable String id, @Valid @RequestBody UpdateActivityRequest updateActivityRequest) {
        String userId = SecurityContextHolder.getContext()
              .getAuthentication()
              .getPrincipal()
              .toString();

        var existingActivity = activityRepository.findById(UUID.fromString(id)).orElseThrow(() -> new RestNotFoundException("Erro ao alterar, atividade nao encontrada!"));

        if (updateActivityRequest.duration() != null) {
          existingActivity.setDuration(updateActivityRequest.duration());
        }

        if (updateActivityRequest.name() != null) {
          existingActivity.setName(updateActivityRequest.name());
        }

        if (updateActivityRequest.dataDia() != null) {
          existingActivity.setDataDia(updateActivityRequest.dataDia());
        }

        if (updateActivityRequest.note() != null) {
          existingActivity.setLembrete(updateActivityRequest.note());
        }

        existingActivity.setUserId(UUID.fromString(userId));
        activityRepository.save(existingActivity);
        return existingActivity.toEntityModel();
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Deletar atividade.",
        description = "Endpoint que recebe um id e deleta uma atividade." 
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "sem conteudo/deletada com sucesso"),
        @ApiResponse(responseCode = "404", description = "nao existe atividade com o id informado")
    })
    public ResponseEntity<Activity> delete(@PathVariable UUID id) {
        String userId = SecurityContextHolder.getContext()
              .getAuthentication()
              .getPrincipal()
              .toString();

        var activity = activityRepository.findById(id).orElseThrow(() -> new RestNotFoundException("Erro ao apagar, atividade nao encontrada!"));
        activityRepository.delete(activity);
        return ResponseEntity.noContent().build();
    }
}
