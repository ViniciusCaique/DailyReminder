package br.com.fiap.dailyreminder.modules.users.infrastructure.repositories;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

import br.com.fiap.dailyreminder.modules.users.domain.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserTokenRepository extends JpaRepository<UserToken, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select token from UserToken token where token.tokenHash = :hash and token.valid = true")
    Optional<UserToken> findForRotation(@Param("hash") String hash);

    Optional<UserToken> findByTokenHashAndValidTrue(String tokenHash);
    @org.springframework.data.jpa.repository.Modifying
    @Query("update UserToken token set token.valid = false where token.user.id = :id")
    void invalidateForUser(@Param("id") java.util.UUID id);
}
