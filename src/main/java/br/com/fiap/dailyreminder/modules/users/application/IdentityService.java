package br.com.fiap.dailyreminder.modules.users.application;

import br.com.fiap.dailyreminder.exceptions.*;
import br.com.fiap.dailyreminder.modules.users.domain.*;
import br.com.fiap.dailyreminder.modules.users.infrastructure.repositories.*;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;
import java.util.UUID;

@Service
@Transactional
public class IdentityService {
    private final UserRepository users;
    private final UserTokenRepository refreshTokens;
    private final AccountTokenRepository accountTokens;
    private final TokenJwtService jwt;
    private final SaveRefreshTokenUseCase saveRefresh;
    private final PasswordEncoder encoder;
    private final AccountMail mail;
    private final Clock clock;
    private final String frontend;
    private final boolean requireVerification;
    public IdentityService(UserRepository users, UserTokenRepository refreshTokens, AccountTokenRepository accountTokens, TokenJwtService jwt, SaveRefreshTokenUseCase saveRefresh, PasswordEncoder encoder, AccountMail mail, Clock clock, @Value("${app.frontend-url:http://localhost:3000}") String frontend, @Value("${app.auth.require-verification:false}") boolean requireVerification) {
        this.users = users; this.refreshTokens = refreshTokens; this.accountTokens = accountTokens; this.jwt = jwt; this.saveRefresh = saveRefresh; this.encoder = encoder; this.mail = mail; this.clock = clock; this.frontend = frontend; this.requireVerification = requireVerification;
    }
    private String encode(String password) {
        if (password.getBytes(java.nio.charset.StandardCharsets.UTF_8).length > 72) throw new IllegalArgumentException("Password must fit within 72 UTF-8 bytes");
        return encoder.encode(password);
    }
    public record Profile(String name, String email, boolean emailVerified) {}
    public record Tokens(String token, String refreshToken) {}
    public Profile profile(UUID userId) { return profile(users.findById(userId).orElseThrow(() -> new InvalidCredentials("Invalid session"))); }
    public Profile updateProfile(UUID userId, String name) {
        var user = users.findById(userId).orElseThrow(() -> new InvalidCredentials("Invalid session"));
        user.setName(name.trim());
        return profile(users.save(user));
    }
    private Profile profile(User user) { return new Profile(user.getName(), user.getEmail(), user.isEmailVerified()); }
    public Profile register(String name, String email, String password) {
        if (users.findByEmailIgnoreCase(email.trim()).isPresent()) throw new EmailAlreadyRegistered("Email already registered");
        var user = new User(); user.setName(name.trim()); user.setEmail(email.trim().toLowerCase(java.util.Locale.ROOT)); user.setPassword(encode(password));
        try { users.saveAndFlush(user); } catch (org.springframework.dao.DataIntegrityViolationException exception) { throw new EmailAlreadyRegistered("Email already registered"); }
        issue(user, "VERIFY_EMAIL");
        return profile(user);
    }
    public Tokens signIn(String email, String password) {
        var user = users.findByEmailIgnoreCase(email.trim()).orElseThrow(() -> new InvalidCredentials("Invalid credentials"));
        if (!encoder.matches(password, user.getPassword())) throw new InvalidCredentials("Invalid credentials");
        if (requireVerification && !user.isEmailVerified()) throw new EmailNotVerified();
        return tokens(user);
    }
    private Tokens tokens(User user) {
        var token = jwt.generateToken(user.getId()); var refresh = jwt.generateRefreshToken(user.getId());
        saveRefresh.execute(user, refresh.token(), refresh.expiresAt());
        return new Tokens(token.token(), refresh.token());
    }
    public Tokens refresh(String token) {
        try {
            var decoded = jwt.validateRefresh(token);
            var stored = refreshTokens.findForRotation(jwt.hashToken(token)).orElseThrow(() -> new InvalidCredentials("Invalid refresh token"));
            if (!stored.getUser().getId().toString().equals(decoded.getSubject()) || !stored.getExpiresAt().isAfter(Instant.now(clock))) throw new InvalidCredentials("Invalid refresh token");
            if (requireVerification && !stored.getUser().isEmailVerified()) throw new EmailNotVerified();
            stored.setValid(false); refreshTokens.save(stored);
            return tokens(stored.getUser());
        } catch (JWTVerificationException | IllegalArgumentException exception) { throw new InvalidCredentials("Invalid refresh token"); }
    }
    public void requestReset(String email) { users.findByEmailIgnoreCase(email.trim()).ifPresent(user -> issue(user, "RESET_PASSWORD")); }
    public void requestVerification(String email) { users.findByEmailIgnoreCase(email.trim()).filter(user -> !user.isEmailVerified()).ifPresent(user -> issue(user, "VERIFY_EMAIL")); }
    private void issue(User user, String purpose) {
        accountTokens.invalidate(user.getId(), purpose);
        String raw = UUID.randomUUID().toString() + UUID.randomUUID();
        var token = new AccountToken(); token.setUserId(user.getId()); token.setPurpose(purpose); token.setTokenHash(jwt.hashToken(raw));
        token.setExpiresAt(Instant.now(clock).plus(purpose.equals("RESET_PASSWORD") ? Duration.ofMinutes(30) : Duration.ofDays(1))); accountTokens.save(token);
        String path = purpose.equals("RESET_PASSWORD") ? "/auth/reset-password" : "/auth/verify-email";
        String subject = purpose.equals("RESET_PASSWORD") ? "Reset your DailyReminder password" : "Verify your DailyReminder email";
        mail.send(user.getEmail(), subject, subject + "\n\n" + frontend + path + "#token=" + raw + "\n\nIf you did not request this, you can ignore this email.");
    }
    private User consume(String raw, String purpose) {
        var token = accountTokens.findUsable(jwt.hashToken(raw), purpose).filter(item -> item.getExpiresAt().isAfter(Instant.now(clock))).orElseThrow(() -> new IllegalArgumentException("This link has expired or has already been used"));
        token.setUsed(true); accountTokens.save(token);
        return users.findById(token.getUserId()).orElseThrow(() -> new InvalidCredentials("Invalid account"));
    }
    public void reset(String token, String password) {
        var user = consume(token, "RESET_PASSWORD"); user.setPassword(encode(password)); user.setCredentialVersion(user.getCredentialVersion() + 1); users.save(user);
        refreshTokens.invalidateForUser(user.getId());
    }
    public void verify(String token) { var user = consume(token, "VERIFY_EMAIL"); user.setEmailVerified(true); users.save(user); }
}
