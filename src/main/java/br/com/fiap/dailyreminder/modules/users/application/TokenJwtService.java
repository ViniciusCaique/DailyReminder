package br.com.fiap.dailyreminder.modules.users.application;
import br.com.fiap.dailyreminder.services.JwtToken;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.HexFormat;
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
    @Value("${app.auth.require-verification:false}") boolean requireVerification;

    public JwtToken generateToken(UUID id) {

        Algorithm alg = Algorithm.HMAC256(secret);
        var expiresAt = Instant.now().plus(Duration.ofHours(2));

        var token = JWT.create()
                    .withExpiresAt(expiresAt)
                    .withClaim("type", "access")
                    .withClaim("version", userRepository.findById(id).orElseThrow().getCredentialVersion())
                    .withSubject(id.toString())
                    .withIssuer("Daily")
                    .sign(alg);

        return new JwtToken(token, expiresAt);
    }

    public JwtToken generateRefreshToken(UUID id) {

      Algorithm alg = Algorithm.HMAC256(secret);
      var expiresAt = Instant.now().plus(Duration.ofDays(7));

      var token = JWT.create()
              .withExpiresAt(expiresAt)
              .withJWTId(UUID.randomUUID().toString())
              .withClaim("type", "refresh")
              .withSubject(id.toString())
              .withIssuer("Daily")
              .sign(alg);

      return new JwtToken(token, expiresAt);
    }


    public DecodedJWT validateAccess(String token) {
      var decoded = JWT.require(Algorithm.HMAC256(secret)).withIssuer("Daily").withClaim("type", "access").build().verify(token);
      var user = userRepository.findById(UUID.fromString(decoded.getSubject())).orElseThrow(() -> new com.auth0.jwt.exceptions.JWTVerificationException("Invalid session"));
      if (requireVerification && !user.isEmailVerified()) throw new com.auth0.jwt.exceptions.JWTVerificationException("Email verification required");
      if (!java.util.Objects.equals(decoded.getClaim("version").asInt(), user.getCredentialVersion())) throw new com.auth0.jwt.exceptions.JWTVerificationException("Invalid session");
      return decoded;
    }

    public DecodedJWT validateRefresh(String token) {
      return JWT.require(Algorithm.HMAC256(secret)).withIssuer("Daily").withClaim("type", "refresh").build().verify(token);
    }

    public String hashToken(String token) {
      try {
        var digest = MessageDigest.getInstance("SHA-256");
        var hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
        return HexFormat.of().formatHex(hash);
      } catch (NoSuchAlgorithmException exception) {
        throw new IllegalStateException("SHA-256 algorithm not available", exception);
      }
    }
}
