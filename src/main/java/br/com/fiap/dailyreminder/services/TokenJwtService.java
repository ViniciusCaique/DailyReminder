package br.com.fiap.dailyreminder.services;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import br.com.fiap.dailyreminder.modules.users.infrastructure.repositories.UserRepository;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;


@Service
public class TokenJwtService {

    @Value("${jwt.secret}")
    String secret;

    @Autowired
    UserRepository userRepository;

    public JwtToken generateToken(UUID id) {

        Algorithm alg = Algorithm.HMAC256(secret);

        var token = JWT.create()
                    .withExpiresAt(Instant.now().plus(Duration.ofHours(2)))
                    .withSubject(id.toString())
                    .withIssuer("Daily")
                    .sign(alg);

        return new JwtToken(token);
    }

    public DecodedJWT validate(String token) {

        Algorithm alg = Algorithm.HMAC256(secret);

      return JWT.require(alg)
                  .withIssuer("Daily")
                  .build()
                  .verify(token);
    }
}
