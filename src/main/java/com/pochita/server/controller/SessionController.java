package com.pochita.server.controller;

import com.pochita.server.dto.SessionDtos.FinishSessionRequest;
import com.pochita.server.dto.SessionDtos.SessionResponse;
import com.pochita.server.dto.SessionDtos.StartSessionRequest;
import com.pochita.server.service.SessionService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sessions")
public class SessionController {

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping("/start")
    public SessionResponse startSession(@Valid @RequestBody StartSessionRequest request) {
        return SessionResponse.from(sessionService.startSession(request));
    }

    @PostMapping("/{sessionId}/finish")
    public SessionResponse finishSession(@PathVariable String sessionId, @RequestBody(required = false) FinishSessionRequest request) {
        return SessionResponse.from(sessionService.finishSession(sessionId, request));
    }

    @GetMapping("/users/{userId}")
    public List<SessionResponse> getUserSessions(@PathVariable String userId) {
        return sessionService.getUserSessions(userId).stream().map(SessionResponse::from).toList();
    }

    @GetMapping("/users/{userId}/today")
    public List<SessionResponse> getTodaySessions(@PathVariable String userId) {
        return sessionService.getTodaySessions(userId).stream().map(SessionResponse::from).toList();
    }
}
