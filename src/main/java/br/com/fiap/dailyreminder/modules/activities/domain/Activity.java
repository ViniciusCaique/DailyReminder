package br.com.fiap.dailyreminder.modules.activities.domain;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

import br.com.fiap.dailyreminder.modules.notes.domain.Note;
import jakarta.persistence.*;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.hateoas.EntityModel;

import br.com.fiap.dailyreminder.modules.activities.infrastructure.controllers.ActivityController;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


@Data
@AllArgsConstructor
@ToString
@NoArgsConstructor
@Builder
@Entity
@Table(name = "tb_activity")
public class Activity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private int duration;
    private LocalDate dataDia;
    private String name;

    @CreationTimestamp
    private Date created_at;

    @ManyToOne
    private Note lembrete;

    @Column(name = "userId", nullable = false)
    private UUID userId;


    public EntityModel<Activity> toEntityModel(){
        // if(this.getLembrete() != null) {
        //     return EntityModel.of(
        //         this,
        //         linkTo(methodOn(AtividadeController.class).show(id)).withSelfRel(),
        //         linkTo(methodOn(AtividadeController.class).index(null, Pageable.unpaged())).withRel("all"),
        //         // linkTo(methodOn(AtividadeController.class).show(this.getLembrete().getId())).withRel("lembrete"),
        //         linkTo(methodOn(AtividadeController.class).show(id)).withRel("delete")
        //     );
        // } else {
        //     return EntityModel.of(
        //         this,
        //         linkTo(methodOn(AtividadeController.class).show(id)).withSelfRel(),
        //         linkTo(methodOn(AtividadeController.class).index(null, Pageable.unpaged())).withRel("all"),
        //         linkTo(methodOn(AtividadeController.class).show(id)).withRel("delete")
        //     );
        // }

            return EntityModel.of(
                this,
                linkTo(methodOn(ActivityController.class).show(id)).withSelfRel(),
                // linkTo(methodOn(AtividadeController.class).index(null, Pageable.unpaged())).withRel("all"),
                linkTo(methodOn(ActivityController.class).show(id)).withRel("delete")
            );
    }

}
