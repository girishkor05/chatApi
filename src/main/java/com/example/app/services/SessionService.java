package com.example.app.services;

import com.example.app.models.ActiveSession;
import com.example.app.models.SignalingMessage;
import com.example.app.repositories.ActiveSessionRepository;
import com.example.app.repositories.QueueUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class SessionService {

    private final ActiveSessionRepository activeSessionRepository;

    private final QueueUserRepository queueUserRepository;

    private final SimpMessagingTemplate messagingTemplate;

    public SessionService(ActiveSessionRepository activeSessionRepository, QueueUserRepository queueUserRepository, SimpMessagingTemplate messagingTemplate) {
        this.activeSessionRepository = activeSessionRepository;
        this.queueUserRepository = queueUserRepository;
        this.messagingTemplate = messagingTemplate;
    }

    public void handleSignaling(SignalingMessage message) {
        messagingTemplate.convertAndSend("/topic/session/" + message.getTargetSessionId(), message);
    }

    public void disconnectSession(String sessionId, String targetSessionId) {
        // Find and remove active session
        List<ActiveSession> sessions = activeSessionRepository.findByUserSessionId1OrUserSessionId2(sessionId, sessionId);
        sessions.forEach(session -> {
            session.setActive(false);
            activeSessionRepository.save(session);
        });

        // Notify other user about disconnection
        if (targetSessionId != null) {
            SignalingMessage message = new SignalingMessage();
            message.setType("PEER_DISCONNECTED");
            message.setSessionId(sessionId);
            messagingTemplate.convertAndSend("/topic/session/" + targetSessionId, message);
        }
    }

    public void handleUserDisconnect(String sessionId) {
        // Remove from queue if present
        queueUserRepository.deleteById(sessionId);

        // Find and process active sessions
        List<ActiveSession> sessions = activeSessionRepository.findByUserSessionId1OrUserSessionId2(sessionId, sessionId);
        sessions.forEach(session -> {
            String otherSessionId = session.getUserSessionId1().equals(sessionId)
                    ? session.getUserSessionId2()
                    : session.getUserSessionId1();

            // Notify other user
            SignalingMessage message = new SignalingMessage();
            message.setType("PEER_DISCONNECTED");
            message.setSessionId(sessionId);
            messagingTemplate.convertAndSend("/topic/session/" + otherSessionId, message);

            // Update session status
            session.setActive(false);
            activeSessionRepository.save(session);

        });
    }

    // CRUD Support
    public ActiveSession saveActiveSession(ActiveSession session) {
        return activeSessionRepository.save(session);
    }

    public ActiveSession findActiveSessionById(String id) {
        return activeSessionRepository.findById(id).orElse(null);
    }

    public List<ActiveSession> findAllActiveSessions() {
        return activeSessionRepository.findAll();
    }

    public List<ActiveSession> findActiveSessionByUsers(String sessionId1, String sessionId2) {
        return activeSessionRepository.findByUserSessionId1OrUserSessionId2(sessionId1, sessionId2);
    }

    public void deleteActiveSession(String id) {
        activeSessionRepository.deleteById(id);
    }

}
