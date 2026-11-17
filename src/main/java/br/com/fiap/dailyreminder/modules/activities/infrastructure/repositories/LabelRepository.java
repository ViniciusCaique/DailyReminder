package br.com.fiap.dailyreminder.modules.activities.infrastructure.repositories;
import br.com.fiap.dailyreminder.modules.activities.domain.Label;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface LabelRepository extends JpaRepository<Label, UUID> {
    List<Label> findByUserIdOrderByNameAsc(UUID userId);
    Optional<Label> findByIdAndUserId(UUID id, UUID userId);
    boolean existsByUserIdAndNameIgnoreCaseAndIdNot(UUID userId, String name, UUID id);
}
