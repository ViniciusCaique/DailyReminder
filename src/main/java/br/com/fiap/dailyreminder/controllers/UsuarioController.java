package br.com.fiap.dailyreminder.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.fiap.dailyreminder.models.Credencial;
import br.com.fiap.dailyreminder.models.Usuario;
import br.com.fiap.dailyreminder.repository.UsuarioRepository;
import br.com.fiap.dailyreminder.services.TokenJwtService;
import jakarta.validation.Valid;

@RestController
public class UsuarioController {

    @Autowired
    UsuarioRepository usuarioRepository;
    
    @Autowired
    AuthenticationManager manager;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    TokenJwtService tokenJwtService;

    @PostMapping("/api/registrar")
    public ResponseEntity<Usuario> registrar(@RequestBody @Valid Usuario usuario) {
        usuario.setSenha(encoder.encode(usuario.getSenha()));
        usuarioRepository.save(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
    }

    @PostMapping("/api/login")
    public ResponseEntity<Object> login(@RequestBody @Valid Credencial credencial) {
        manager.authenticate(credencial.toAuthentication());
        var token = tokenJwtService.generateToken(credencial);
        return ResponseEntity.ok(token);
    }
    
}
