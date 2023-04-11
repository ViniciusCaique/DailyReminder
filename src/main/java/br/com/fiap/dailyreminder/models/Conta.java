package br.com.fiap.dailyreminder.models;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



// @Getter
// @Setter
// @ToString
// @AllArgsConstructor
// @EqualsAndHashCode
// da pra substituir tudo isso pelo @Data, que tem tudo isso dentro dele


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Conta {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @JsonProperty(access = Access.WRITE_ONLY)
    private BigDecimal saldoInicial;

    private String icone;

    @JsonIgnore
    private Boolean ativa;
    
}
