package br.com.fiap.dailyreminder.models;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;

import br.com.fiap.dailyreminder.controllers.AtividadeController;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@AllArgsConstructor
@ToString
@NoArgsConstructor
@Builder
@Entity
public class Atividade {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(value = 0, message = "Nao existe tempo negativo!") 
    @NotNull
    private int duracao;

    @NotNull
    private LocalDate data;

    @NotBlank @Size(min = 3, max = 255)
    private String atividade;

    @CreationTimestamp
    private Date created_at;

    @ManyToOne
    private Lembrete lembrete;

    public EntityModel<Atividade> toEntityModel(){
        if(this.getLembrete() != null) {
            return EntityModel.of(
                this,
                linkTo(methodOn(AtividadeController.class).show(id)).withSelfRel(),
                linkTo(methodOn(AtividadeController.class).index(null, Pageable.unpaged())).withRel("all"),
                linkTo(methodOn(AtividadeController.class).show(this.getLembrete().getId())).withRel("lembrete"),
                linkTo(methodOn(AtividadeController.class).show(id)).withRel("delete")
            );
        } else {
            return EntityModel.of(
                this,
                linkTo(methodOn(AtividadeController.class).show(id)).withSelfRel(),
                linkTo(methodOn(AtividadeController.class).index(null, Pageable.unpaged())).withRel("all"),
                linkTo(methodOn(AtividadeController.class).show(id)).withRel("delete")
            );
        }

    }

}
