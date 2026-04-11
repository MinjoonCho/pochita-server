package com.pochita.server.service;

import com.pochita.server.common.ApiException;
import com.pochita.server.common.IdGenerator;
import com.pochita.server.domain.DistractionSession;
import com.pochita.server.dto.SessionDtos.FinishSessionRequest;
import com.pochita.server.dto.SessionDtos.StartSessionRequest;
import com.pochita.server.repository.DistractionSessionRepository;
import com.pochita.server.repository.UserRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SessionService {

    private final DistractionSessionRepository sessionRepository;
    private final UserRepository userRepository;

    public SessionService(DistractionSessionRepository sessionRepository, UserRepository userRepository) {
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
    }

    public DistractionSession startSession(StartSessionRequest request) {
        ensureUserExists(request.userId());
        DistractionSession session = new DistractionSession(
                IdGenerator.newId(),
                request.userId().trim(),
                request.categoryId().trim(),
                System.currentTimeMillis()
        );
        return sessionRepository.save(session);
    }

    public DistractionSession finishSession(String sessionId, FinishSessionRequest request) {
        DistractionSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "세션을 찾을 수 없습니다."));

        long endTime = System.currentTimeMillis();
        long duration = Math.max(0, (endTime - session.getStartTime()) / 1000);
        session.finish(endTime, duration, request == null ? null : request.note());
        return sessionRepository.save(session);
    }

    @Transactional(readOnly = true)
    public List<DistractionSession> getUserSessions(String userId) {
        return sessionRepository.findByUserIdAndEndTimeIsNotNullOrderByStartTimeDesc(userId);
    }

    @Transactional(readOnly = true)
    public List<DistractionSession> getTodaySessions(String userId) {
        long startOfDay = LocalDate.now()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();

        return getUserSessions(userId).stream()
                .filter(session -> session.getStartTime() >= startOfDay)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DistractionSession> getCompletedSessions() {
        return sessionRepository.findByEndTimeIsNotNull();
    }

    private void ensureUserExists(String userId) {
        if (!userRepository.existsById(userId.trim())) {
            throw new ApiException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.");
        }
    }
}
