package br.com.fiap.dailyreminder.models;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


import java.sql.Date;
import java.time.LocalDate;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;

import br.com.fiap.dailyreminder.controllers.AtividadeController;
import br.com.fiap.dailyreminder.controllers.LembreteController;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Lembrete {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String mensagem;

    
    @Min(value = 0, message = "Nao existe tempo negativo!") 
    @NotNull
    private int duracao;

    public EntityModel<Lembrete> toEntityModel() {
        return EntityModel.of(
            this,
            linkTo(methodOn(LembreteController.class).show(id)).withSelfRel(),
            linkTo(methodOn(LembreteController.class).index(null, Pageable.unpaged())).withRel("all"),
            linkTo(methodOn(LembreteController.class).show(id)).withRel("destroy")
        );
    }

}
