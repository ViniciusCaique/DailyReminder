package br.com.fiap.dailyreminder.services;

import java.time.Instant;

public record JwtToken(String token, Instant expiresAt) {
    
}
