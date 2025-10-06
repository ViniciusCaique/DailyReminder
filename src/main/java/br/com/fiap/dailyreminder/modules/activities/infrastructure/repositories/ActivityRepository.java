package br.com.fiap.dailyreminder.modules.activities.infrastructure.repositories;

import br.com.fiap.dailyreminder.modules.activities.domain.Activity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;
import java.util.UUID;

public interface ActivityRepository extends JpaRepository<Activity, UUID> {

    Page<Activity> findByName(String name, Pageable pageable);
    Optional<Activity> findByUserId(UUID id);
    
}
