package br.com.fiap.dailyreminder.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    private int duracao;
    private LocalDate data;
    private String atividade;

    protected Atividade(){}

    @Override
    public String toString() {
        return "Atividade [duracao=" + duracao + ", data=" + data + ", atividade=" + atividade + "]";
    }
}
