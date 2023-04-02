package br.com.fiap.dailyreminder.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor

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

    // @NotNull Nao Pode ser nulo
    // @NotEmpty Nao pode estar vazio
    // @NotBlank Nao pode ter apenas espacos e o size, para ter um minimo

    @NotBlank @Size(min = 5, max = 255)
    private String atividade;

    protected Atividade(){}

    @Override
    public String toString() {
        return "Atividade [duracao=" + duracao + ", data=" + data + ", atividade=" + atividade + "]";
    }
}
