package br.com.fiap.dailyreminder.modules.users.infrastructure.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.fiap.dailyreminder.modules.users.domain.User;

public interface UserRepository extends JpaRepository<User, UUID>{

    Optional<User> findByEmail(String email);

}
