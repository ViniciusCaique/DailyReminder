package br.com.fiap.dailyreminder.modules.activities.infrastructure.repositories;
import br.com.fiap.dailyreminder.modules.activities.domain.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface ProjectRepository extends JpaRepository<Project, UUID> {
    List<Project> findByUserIdOrderByNameAsc(UUID userId);
    Optional<Project> findByIdAndUserId(UUID id, UUID userId);
    boolean existsByUserIdAndNameIgnoreCaseAndIdNot(UUID userId, String name, UUID id);
}
