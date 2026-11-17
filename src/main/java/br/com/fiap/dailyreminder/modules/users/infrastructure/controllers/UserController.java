package br.com.fiap.dailyreminder.modules.users.infrastructure.controllers;

import br.com.fiap.dailyreminder.modules.users.application.IdentityService;
import br.com.fiap.dailyreminder.modules.users.infrastructure.dtos.request.*;
import br.com.fiap.dailyreminder.shared.CurrentUser;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class UserController {
    private final IdentityService identity;
    public UserController(IdentityService identity) { this.identity = identity; }
    public record ProfileRequest(@NotBlank @Size(max = 255) String name) {}
    @PatchMapping("/profile") public IdentityService.Profile updateProfile(@Valid @RequestBody ProfileRequest request) { return identity.updateProfile(CurrentUser.id(), request.name()); }
    public record EmailRequest(@NotBlank @Email String email) {}
    public record TokenRequest(@NotBlank String token) {}
    public record ResetRequest(@NotBlank String token, @NotBlank @Size(min = 8, max = 72) String password) {}
    @GetMapping public List<IdentityService.Profile> index() { return List.of(identity.profile(CurrentUser.id())); }
    @PostMapping("/sign-up") public ResponseEntity<IdentityService.Profile> signUp(@Valid @RequestBody CreateUserRequest request) { return ResponseEntity.status(201).body(identity.register(request.name(), request.email(), request.password())); }
    @PostMapping("/sign-in") public IdentityService.Tokens signIn(@Valid @RequestBody SignInUserRequest request) { return identity.signIn(request.email(), request.password()); }
    @PostMapping("/refresh") public IdentityService.Tokens refresh(@Valid @RequestBody RefreshTokenRequest request) { return identity.refresh(request.refreshToken()); }
    @PostMapping("/forgot-password") public ResponseEntity<Void> forgot(@Valid @RequestBody EmailRequest request) { identity.requestReset(request.email()); return ResponseEntity.accepted().build(); }
    @PostMapping("/request-verification") public ResponseEntity<Void> requestVerification(@Valid @RequestBody EmailRequest request) { identity.requestVerification(request.email()); return ResponseEntity.accepted().build(); }
    @PostMapping("/reset-password") public ResponseEntity<Void> reset(@Valid @RequestBody ResetRequest request) { identity.reset(request.token(), request.password()); return ResponseEntity.noContent().build(); }
    @PostMapping("/verify-email") public ResponseEntity<Void> verify(@Valid @RequestBody TokenRequest request) { identity.verify(request.token()); return ResponseEntity.noContent().build(); }
}
