package br.com.fiap.dailyreminder.modules.users.infrastructure.repositories;

import br.com.fiap.dailyreminder.modules.users.domain.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserTokenRepository extends JpaRepository<UserToken, UUID> {

    Optional<UserToken> findByTokenHashAndValidTrue(String tokenHash);
}
