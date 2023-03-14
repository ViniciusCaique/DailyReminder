package br.com.fiap.dailyreminder.controllers;

import java.time.LocalDate;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.fiap.dailyreminder.models.Atividade;

@RestController
public class AtividadeController {
    
    @GetMapping("api/atividades")
    public Atividade show(){
        return new Atividade( 120, LocalDate.now(), "correr");
    }

}
