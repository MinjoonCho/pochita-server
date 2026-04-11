package com.pochita.server.repository;

import com.pochita.server.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
