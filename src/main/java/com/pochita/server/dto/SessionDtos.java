package com.pochita.server.dto;

import com.pochita.server.domain.DistractionSession;
import jakarta.validation.constraints.NotBlank;

public final class SessionDtos {

    private SessionDtos() {
    }

    public record StartSessionRequest(
            @NotBlank String userId,
            @NotBlank String categoryId
    ) {
    }

    public record FinishSessionRequest(String note) {
    }

    public record SessionResponse(
            String id,
            String userId,
            String categoryId,
            long startTime,
            Long endTime,
            Long duration,
            String note
    ) {
        public static SessionResponse from(DistractionSession session) {
            return new SessionResponse(
                    session.getId(),
                    session.getUserId(),
                    session.getCategoryId(),
                    session.getStartTime(),
                    session.getEndTime(),
                    session.getDuration(),
                    session.getNote()
            );
        }
    }
}
