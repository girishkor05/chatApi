package com.example.app.controllers;

import com.example.app.models.ActiveSession;
import com.example.app.models.SignalingMessage;
import com.example.app.services.QueueService;
import com.example.app.services.SessionService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.context.event.EventListener;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class SessionController {

    private final SessionService sessionService;
    private final QueueService queueService;
    private static final Logger log = LoggerFactory.getLogger(SessionController.class);

    public SessionController(SessionService sessionService, QueueService queueService) {
        this.sessionService = sessionService;
        this.queueService = queueService;
    }

    // Existing WebSocket handler for signaling
    @MessageMapping("/session.signal")
    public void processSignaling(SignalingMessage message) {
        if (message == null || message.getType() == null || message.getSessionId() == null || message.getTargetSessionId() == null) {
            log.error("Invalid signaling message: missing required fields");
            return;
        }
        log.info("Signaling message: {} from {} to {}", message.getType(), message.getSessionId(), message.getTargetSessionId());
        sessionService.handleSignaling(message);
    }

    // Existing WebSocket handler for session disconnection
    @MessageMapping("/session.disconnect")
    public void disconnectSession(SignalingMessage message) {
        if (message == null || message.getSessionId() == null) {
            log.error("Invalid disconnect request: sessionId is missing");
            return;
        }
        log.info("Session disconnect: {} from {}", message.getSessionId(), message.getTargetSessionId());
        sessionService.disconnectSession(message.getSessionId(), message.getTargetSessionId());
    }

    // Updated WebSocket disconnect event listener
    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        if (sessionId != null) {
            log.info("User disconnected: {}", sessionId);
            sessionService.handleUserDisconnect(sessionId);
            queueService.removeUserFromQueue(sessionId); // Clean up queue entry
        }
    }

    // CRUD: Create an active session
    @PostMapping("/api/sessions")
    @ResponseBody
    public ActiveSession createSession(@RequestBody ActiveSession session) {
        if (session == null || session.getUserSessionId1() == null || session.getUserSessionId2() == null) {
            throw new IllegalArgumentException("Both user session IDs are required");
        }
        if (session.getUserSessionId1().equals(session.getUserSessionId2())) {
            throw new IllegalArgumentException("Cannot create session with same user session IDs");
        }
        if (!sessionService.findActiveSessionByUsers(session.getUserSessionId1(), session.getUserSessionId2()).isEmpty()) {
            throw new IllegalStateException("Active session already exists for these users");
        }
        session.setId(UUID.randomUUID().toString());
        session.setStartedAt(new Date());
        session.setActive(true);
        ActiveSession savedSession = sessionService.saveActiveSession(session);
        log.info("Created active session: {}", savedSession.getId());
        return savedSession;
    }

    // CRUD: Read an active session by ID
    @GetMapping("/api/sessions/{id}")
    @ResponseBody
    public ActiveSession getSession(@PathVariable String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Session ID is required");
        }
        ActiveSession session = sessionService.findActiveSessionById(id);
        if (session == null) {
            throw new IllegalStateException("Session with ID " + id + " not found");
        }
        log.info("Retrieved session: {}", id);
        return session;
    }

    // CRUD: Read all active sessions
    @GetMapping("/api/sessions")
    @ResponseBody
    public List<ActiveSession> getAllSessions() {
        List<ActiveSession> sessions = sessionService.findAllActiveSessions();
        log.info("Retrieved {} active sessions", sessions.size());
        return sessions;
    }

    // CRUD: Update an active session
    @PutMapping("/api/sessions/{id}")
    @ResponseBody
    public ActiveSession updateSession(@PathVariable String id, @RequestBody ActiveSession updatedSession) {
        if (id == null || id.isEmpty() || !id.equals(updatedSession.getId())) {
            throw new IllegalArgumentException("Session ID is invalid or does not match request body");
        }
        ActiveSession existingSession = sessionService.findActiveSessionById(id);
        if (existingSession == null) {
            throw new IllegalStateException("Session with ID " + id + " not found");
        }
        existingSession.setUserSessionId1(updatedSession.getUserSessionId1() != null ? updatedSession.getUserSessionId1() : existingSession.getUserSessionId1());
        existingSession.setUserSessionId2(updatedSession.getUserSessionId2() != null ? updatedSession.getUserSessionId2() : existingSession.getUserSessionId2());
        existingSession.setActive(updatedSession.isActive());
        existingSession.setStartedAt(updatedSession.getStartedAt() != null ? updatedSession.getStartedAt() : existingSession.getStartedAt());
        ActiveSession savedSession = sessionService.saveActiveSession(existingSession);
        log.info("Updated session: {}", id);
        return savedSession;
    }

    // CRUD: Delete an active session
    @DeleteMapping("/api/sessions/{id}")
    @ResponseBody
    public void deleteSession(@PathVariable String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Session ID is required");
        }
        ActiveSession session = sessionService.findActiveSessionById(id);
        if (session == null) {
            throw new IllegalStateException("Session with ID " + id + " not found");
        }
        sessionService.deleteActiveSession(id);
        log.info("Deleted session: {}", id);
    }
}