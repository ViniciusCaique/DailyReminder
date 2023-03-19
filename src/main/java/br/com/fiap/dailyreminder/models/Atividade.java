package br.com.fiap.dailyreminder.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
public class Atividade {
    
    private Long id;
    private int duracao;
    private LocalDate data;
    private String atividade;

    @Override
    public String toString() {
        return "Atividade [duracao=" + duracao + ", data=" + data + ", atividade=" + atividade + "]";
    }
}
