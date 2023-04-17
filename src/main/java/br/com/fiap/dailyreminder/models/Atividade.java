package br.com.fiap.dailyreminder.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
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

    @ManyToOne
    private Lembrete lembrete;

}
