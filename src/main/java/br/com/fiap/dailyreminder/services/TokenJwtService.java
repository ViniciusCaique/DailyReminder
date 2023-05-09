package br.com.fiap.dailyreminder.services;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import br.com.fiap.dailyreminder.models.Credencial;
import br.com.fiap.dailyreminder.models.Usuario;
import br.com.fiap.dailyreminder.repository.UsuarioRepository;


@Service
public class TokenJwtService {

    @Value("${jwt.secret}")
    String secret;

    @Autowired
    UsuarioRepository usuarioRepository;

    public JwtToken generateToken(Credencial credencial) {

        Algorithm alg = Algorithm.HMAC256("mysecrety");

        var token = JWT.create()
                    .withExpiresAt(Instant.now().plus(2, ChronoUnit.HOURS))
                    .withSubject(credencial.email())
                    .withIssuer("Daily")
                    .sign(alg);
        return new JwtToken(token);
    }

    public Usuario validate(String token) {

        Algorithm alg = Algorithm.HMAC256("mysecrety");

        var email = JWT.require(alg)
                    .withIssuer("Daily")
                    .build()
                    .verify(token)
                    .getSubject();

        return usuarioRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Token invalido!"));
    }
}
