package br.com.fiap.dailyreminder.modules.activities.infrastructure.repositories;

import br.com.fiap.dailyreminder.modules.activities.domain.Activity;
import br.com.fiap.dailyreminder.modules.activities.infrastructure.dtos.response.ActivityResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ActivityRepository extends JpaRepository<Activity, UUID> {

    Page<Activity> findByName(String name, Pageable pageable);
    Optional<List<Activity>> findByUserId(UUID id);

    @Query("""
            select new br.com.fiap.dailyreminder.modules.activities.infrastructure.dtos.response.ActivityResponse(
                cast(activity.id as string),
                activity.duration,
                activity.dataDia,
                activity.name,
                cast(note.id as string),
                cast(user.id as string)
            )
            from Activity activity
            left join activity.lembrete note
            left join activity.user user
            """)
    List<ActivityResponse> findAllResponses();

    @Query("""
            select new br.com.fiap.dailyreminder.modules.activities.infrastructure.dtos.response.ActivityResponse(
                cast(activity.id as string),
                activity.duration,
                activity.dataDia,
                activity.name,
                cast(note.id as string),
                cast(user.id as string)
            )
            from Activity activity
            left join activity.lembrete note
            left join activity.user user
            where user.id = :userId
            """)
    List<ActivityResponse> findResponsesByUserId(@Param("userId") UUID userId);

    @Query("""
            select new br.com.fiap.dailyreminder.modules.activities.infrastructure.dtos.response.ActivityResponse(
                cast(activity.id as string),
                activity.duration,
                activity.dataDia,
                activity.name,
                cast(note.id as string),
                cast(user.id as string)
            )
            from Activity activity
            left join activity.lembrete note
            left join activity.user user
            where activity.id = :id
            """)
    Optional<ActivityResponse> findResponseById(@Param("id") UUID id);
    
}
