package br.com.fiap.dailyreminder.modules.users.infrastructure.controllers;

import br.com.fiap.dailyreminder.exceptions.EmailAlreadyRegistered;
import br.com.fiap.dailyreminder.exceptions.InvalidCredentials;
import br.com.fiap.dailyreminder.modules.users.application.SaveRefreshTokenUseCase;
import br.com.fiap.dailyreminder.modules.users.domain.User;
import br.com.fiap.dailyreminder.modules.users.infrastructure.dtos.request.CreateUserRequest;
import br.com.fiap.dailyreminder.modules.users.infrastructure.dtos.request.RefreshTokenRequest;
import br.com.fiap.dailyreminder.modules.users.infrastructure.dtos.request.SignInUserRequest;
import br.com.fiap.dailyreminder.modules.users.infrastructure.dtos.response.CreateUserResponse;
import br.com.fiap.dailyreminder.modules.users.infrastructure.dtos.response.RefreshTokenResponse;
import br.com.fiap.dailyreminder.modules.users.infrastructure.dtos.response.SignInUserResponse;
import br.com.fiap.dailyreminder.modules.users.infrastructure.repositories.UserRepository;
import br.com.fiap.dailyreminder.modules.users.infrastructure.repositories.UserTokenRepository;
import br.com.fiap.dailyreminder.services.TokenJwtService;
import com.auth0.jwt.exceptions.JWTVerificationException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "Auth")
@RequestMapping("/api/auth")
public class UserController {

    private final UserRepository userRepository;
    private final TokenJwtService tokenJwtService;
    private final UserTokenRepository userTokenRepository;
    private final PasswordEncoder encoder;
    private final SaveRefreshTokenUseCase saveRefreshTokenUseCase;

    public UserController(
            UserRepository userRepository,
            UserTokenRepository userTokenRepository,
            TokenJwtService tokenJwtService,
            SaveRefreshTokenUseCase saveRefreshTokenUseCase,
            PasswordEncoder passwordEncoder
    ) {
      this.userRepository = userRepository;
      this.userTokenRepository = userTokenRepository;
      this.tokenJwtService = tokenJwtService;
      this.saveRefreshTokenUseCase = saveRefreshTokenUseCase;
      this.encoder = passwordEncoder;
    }

    @GetMapping
    @Operation(
            summary = "Listar usuarios",
            description = "Endpoint que retorna todos os usuarios."
    )
    public List<User> index(){
      return userRepository.findAll();
    }

    @PostMapping("/sign-up")
    @Operation(
            summary = "Cadastrar usuario",
            description = "Endpoint que recebe os dados de um usuario."
    )
    public ResponseEntity<CreateUserResponse> signUp(@RequestBody @Valid CreateUserRequest createUserRequest) {
        User user = new User();
        BeanUtils.copyProperties(createUserRequest, user);

        var existingEmail = userRepository.findByEmail(user.getEmail());

        if (existingEmail.isPresent()) {
          throw new EmailAlreadyRegistered("Email already registered");
        }

        user.setPassword(encoder.encode(user.getPassword()));

        User savedUser = userRepository.save(user);

        var response = new CreateUserResponse(
            savedUser.getName(),
            savedUser.getEmail()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/sign-in")
    @Operation(
            summary = "Logar um usuario",
            description = "Endpoint que recebe os dados de um usuario."
    )
    public ResponseEntity<SignInUserResponse> signIn(@RequestBody @Valid SignInUserRequest signInUserRequest) {
        User user = new User();
        BeanUtils.copyProperties(signInUserRequest, user);

        var existingUser = userRepository.findByEmail(user.getEmail()).orElseThrow(() -> new InvalidCredentials("Email or password may be incorrect"));

        var passwordMatches = encoder.matches(user.getPassword(), existingUser.getPassword());

        if (!passwordMatches) {
          throw new InvalidCredentials("Email or password may be incorrect");
        }

        var token = tokenJwtService.generateToken(existingUser.getId());
        var refreshToken = tokenJwtService.generateRefreshToken(existingUser.getId());
        saveRefreshTokenUseCase.execute(existingUser, refreshToken.token(), refreshToken.expiresAt());

        var response = new SignInUserResponse(token.token(), refreshToken.token());

        return ResponseEntity.status(200).body(response);
    }

    @PostMapping("/refresh")
    @Operation(
            summary = "Atualizar token",
            description = "Endpoint que recebe um refresh token valido e retorna novos tokens."
    )
    public ResponseEntity<RefreshTokenResponse> refreshToken(@RequestBody @Valid RefreshTokenRequest refreshTokenRequest) {
      try {
        var requestRefreshToken = refreshTokenRequest.refreshToken();
        var decodedRefreshToken = tokenJwtService.validate(requestRefreshToken);
        var tokenHash = tokenJwtService.hashToken(requestRefreshToken);
        var storedRefreshToken = userTokenRepository.findByTokenHashAndValidTrue(tokenHash)
                .orElseThrow(() -> new InvalidCredentials("Invalid refresh token"));
        var userId = UUID.fromString(decodedRefreshToken.getSubject());

        if (!storedRefreshToken.getUser().getId().equals(userId)) {
          throw new InvalidCredentials("Invalid refresh token");
        }

        storedRefreshToken.setValid(false);
        userTokenRepository.save(storedRefreshToken);

        var token = tokenJwtService.generateToken(userId);
        var refreshToken = tokenJwtService.generateRefreshToken(userId);
        saveRefreshTokenUseCase.execute(storedRefreshToken.getUser(), refreshToken.token(), refreshToken.expiresAt());

        var response = new RefreshTokenResponse(token.token(), refreshToken.token());

        return ResponseEntity.status(200).body(response);
      } catch (JWTVerificationException | IllegalArgumentException exception) {
        throw new InvalidCredentials("Invalid refresh token");
      }
    }
}
