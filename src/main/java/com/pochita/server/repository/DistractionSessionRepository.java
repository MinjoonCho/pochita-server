package com.pochita.server.repository;

import com.pochita.server.domain.DistractionSession;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DistractionSessionRepository extends JpaRepository<DistractionSession, String> {
    List<DistractionSession> findByUserIdOrderByStartTimeDesc(String userId);
    List<DistractionSession> findByUserIdAndEndTimeIsNotNullOrderByStartTimeDesc(String userId);
    List<DistractionSession> findByEndTimeIsNotNull();
}
