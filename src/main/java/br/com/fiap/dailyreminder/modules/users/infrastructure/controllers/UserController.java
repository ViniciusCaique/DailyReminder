package br.com.fiap.dailyreminder.modules.users.infrastructure.controllers;

import br.com.fiap.dailyreminder.exceptions.EmailAlreadyRegistered;
import br.com.fiap.dailyreminder.exceptions.InvalidCredentials;
import br.com.fiap.dailyreminder.modules.users.infrastructure.dtos.request.CreateUserRequest;
import br.com.fiap.dailyreminder.modules.users.infrastructure.dtos.request.SignInUserRequest;
import br.com.fiap.dailyreminder.modules.users.infrastructure.dtos.response.CreateUserResponse;
import br.com.fiap.dailyreminder.modules.users.infrastructure.dtos.response.SignInUserResponse;
import br.com.fiap.dailyreminder.modules.users.infrastructure.repositories.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import br.com.fiap.dailyreminder.modules.users.domain.User;

import br.com.fiap.dailyreminder.services.TokenJwtService;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@Tag(name = "Auth")
@RequestMapping("/api/auth")
public class UserController {

    @Autowired
    UserRepository userRepository;
    
    @Autowired
    AuthenticationManager manager;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    TokenJwtService tokenJwtService;

    @GetMapping
    @Operation(
            summary = "Listar usuários",
            description = "Endpoint que retorna todos os usuários."
    )
    public List<User> index(){
      return userRepository.findAll();
    }

    @PostMapping("/sign-up")
    @Operation(
            summary = "Cadastrar usuário",
            description = "Endpoint que recebe os dados de um usuário."
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
            summary = "Logar um usuário",
            description = "Endpoint que recebe os dados de um usuário."
    )
    public ResponseEntity<SignInUserResponse> signIn(@RequestBody @Valid SignInUserRequest signInUserRequest) {
//        manager.authenticate(credencial.toAuthentication());

        User user = new User();
        BeanUtils.copyProperties(signInUserRequest, user);

        var existingUser = userRepository.findByEmail(user.getEmail()).orElseThrow(() -> new InvalidCredentials("Email or password may be incorrect"));

        var passwordMatches = encoder.matches(user.getPassword(), existingUser.getPassword());

        if (!passwordMatches) {
          throw new InvalidCredentials("Email or password may be incorrect");
        }

        var token = tokenJwtService.generateToken(existingUser.getId());

        var response = new SignInUserResponse(token.token());

        return ResponseEntity.status(200).body(response);
    }
    
}
