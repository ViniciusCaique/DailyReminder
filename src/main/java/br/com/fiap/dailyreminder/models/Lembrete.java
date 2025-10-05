package br.com.fiap.dailyreminder.models;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


import jakarta.persistence.*;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;

import br.com.fiap.dailyreminder.controllers.LembreteController;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tb_notes")
public class Lembrete implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String message;

//    @Min(value = 0, message = "Nao existe tempo negativo!")
    private int duration;

    public EntityModel<Lembrete> toEntityModel() {
        return EntityModel.of(
            this,
            linkTo(methodOn(LembreteController.class).show(id)).withSelfRel(),
            linkTo(methodOn(LembreteController.class).index(null, Pageable.unpaged())).withRel("all"),
            linkTo(methodOn(LembreteController.class).show(id)).withRel("destroy")
        );
    }

}
