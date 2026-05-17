package br.com.fiap.dailyreminder.modules.users.application;

import br.com.fiap.dailyreminder.modules.users.domain.User;
import br.com.fiap.dailyreminder.modules.users.domain.UserToken;
import br.com.fiap.dailyreminder.modules.users.infrastructure.repositories.UserTokenRepository;
import br.com.fiap.dailyreminder.services.TokenJwtService;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class SaveRefreshTokenUseCase {

  private final TokenJwtService tokenJwtService;
  private final UserTokenRepository userTokenRepository;

  public SaveRefreshTokenUseCase(UserTokenRepository userTokenRepository, TokenJwtService tokenJwtService) {
    this.userTokenRepository = userTokenRepository;
    this.tokenJwtService = tokenJwtService;
  }

  public void execute(User user, String refreshToken, Instant expiresAt) {
    var userToken = new UserToken();
    userToken.setUser(user);
    userToken.setTokenHash(tokenJwtService.hashToken(refreshToken));
    userToken.setExpiresAt(expiresAt);
    userTokenRepository.save(userToken);
  }
}
