package br.com.fiap.dailyreminder.modules.users.infrastructure.repositories;
import br.com.fiap.dailyreminder.modules.users.domain.AccountToken;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;
public interface AccountTokenRepository extends JpaRepository<AccountToken, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select token from AccountToken token where token.tokenHash = :hash and token.purpose = :purpose and token.used = false")
    Optional<AccountToken> findUsable(@Param("hash") String hash, @Param("purpose") String purpose);
    @Modifying @Query("update AccountToken token set token.used = true where token.userId = :userId and token.purpose = :purpose")
    void invalidate(@Param("userId") UUID userId, @Param("purpose") String purpose);
}
