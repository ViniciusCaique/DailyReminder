package br.com.fiap.dailyreminder.models;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

public record Credencial(String email, String password) {

    public Authentication toAuthentication() {
        return new UsernamePasswordAuthenticationToken(email, password);
    }
    
}