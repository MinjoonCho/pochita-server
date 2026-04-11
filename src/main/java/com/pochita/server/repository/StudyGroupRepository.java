package com.pochita.server.repository;

import com.pochita.server.domain.StudyGroup;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyGroupRepository extends JpaRepository<StudyGroup, String> {
    Optional<StudyGroup> findByInviteCode(String inviteCode);
}
