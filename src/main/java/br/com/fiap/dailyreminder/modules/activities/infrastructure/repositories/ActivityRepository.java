package br.com.fiap.dailyreminder.modules.activities.infrastructure.repositories;

import br.com.fiap.dailyreminder.modules.activities.domain.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ActivityRepository extends JpaRepository<Activity, UUID> {
    @EntityGraph(attributePaths = {"labelIds", "checklist"})
    List<Activity> findByUserIdOrderByDataDiaAsc(UUID userId);
    @EntityGraph(attributePaths = {"labelIds", "checklist"})
    Optional<Activity> findByIdAndUserId(UUID id, UUID userId);
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Activity a set a.noteId = null where a.noteId = :id")
    void detachReminder(@Param("id") UUID id);
}
