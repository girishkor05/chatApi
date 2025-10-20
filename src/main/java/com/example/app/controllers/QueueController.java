package com.example.app.controllers;

import com.example.app.models.ActiveSession;
import com.example.app.models.QueueUser;
import com.example.app.models.SignalingMessage;
import com.example.app.services.QueueService;
import com.example.app.services.SessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Controller
public class QueueController {

    private final QueueService queueService;
    private final SessionService sessionService;
    private final SimpMessagingTemplate messagingTemplate;
    private static final Logger log = LoggerFactory.getLogger(QueueController.class);

    public QueueController(QueueService queueService, SessionService sessionService, SimpMessagingTemplate messagingTemplate) {
        this.queueService = queueService;
        this.sessionService = sessionService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/queue.join")
    public void joinQueue(SignalingMessage message) {
        if (message == null || message.getSessionId() == null || message.getSessionId().isEmpty()) {
            log.error("Invalid join request: sessionId is missing");
            return;
        }
        log.info("User joined queue: {}", message.getSessionId());
        queueService.addUserToQueue(message.getSessionId());
    }

    @MessageMapping("/queue.leave")
    public void leaveQueue(SignalingMessage message) {
        if (message == null || message.getSessionId() == null || message.getSessionId().isEmpty()) {
            log.error("Invalid leave request: sessionId is missing");
            return;
        }
        log.info("User left queue: {}", message.getSessionId());
        queueService.removeUserFromQueue(message.getSessionId());
    }

    @PostMapping("/api/queue")
    @ResponseBody
    public QueueUser createQueueUser(@RequestBody QueueUser queueUser) {
        if (queueUser == null || queueUser.getSessionId() == null || queueUser.getSessionId().isEmpty()) {
            throw new IllegalArgumentException("SessionId is required");
        }
        if (queueService.findQueueUserById(queueUser.getSessionId()) != null) {
            throw new IllegalStateException("User with sessionId " + queueUser.getSessionId() + " already exists in queue");
        }

        queueUser.setJoinedAt(new Date());
        queueUser.setMatched(false);
        QueueUser savedUser = queueService.saveQueueUser(queueUser);
        log.info("Created queue user: {}", savedUser.getSessionId());

        List<QueueUser> unmatchedUsers = queueService.findByMatchedFalse();
        if (unmatchedUsers.size() >= 2) {
            Collections.shuffle(unmatchedUsers);
            QueueUser user1 = unmatchedUsers.get(0);
            QueueUser user2 = unmatchedUsers.get(1);

            ActiveSession session = new ActiveSession();
            session.setUserSessionId1(user1.getSessionId());
            session.setUserSessionId2(user2.getSessionId());
            session.setStartedAt(new Date());
            session.setActive(true);
            ActiveSession savedSession = sessionService.saveActiveSession(session);

            user1.setMatched(true);
            user2.setMatched(true);
            queueService.saveQueueUser(user1);
            queueService.saveQueueUser(user2);

            SignalingMessage message1 = new SignalingMessage();
            message1.setType("SESSION_STARTED");
            message1.setSessionId(savedSession.getId());
            message1.setFromSessionId(user1.getSessionId());
            message1.setToSessionId(user2.getSessionId());

            SignalingMessage message2 = new SignalingMessage();
            message2.setType("SESSION_STARTED");
            message2.setSessionId(savedSession.getId());
            message2.setFromSessionId(user2.getSessionId());
            message2.setToSessionId(user1.getSessionId());

            messagingTemplate.convertAndSendToUser(user1.getSessionId(), "/queue/messages", message1);
            messagingTemplate.convertAndSendToUser(user2.getSessionId(), "/queue/messages", message2);

            log.info("Created session {} between {} and {}", savedSession.getId(), user1.getSessionId(), user2.getSessionId());
        }

        return savedUser;
    }

    @GetMapping("/api/queue/{sessionId}")
    @ResponseBody
    public QueueUser getQueueUser(@PathVariable String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            throw new IllegalArgumentException("SessionId is required");
        }
        QueueUser queueUser = queueService.findQueueUserById(sessionId);
        if (queueUser == null) {
            throw new IllegalStateException("Queue user with sessionId " + sessionId + " not found");
        }
        log.info("Retrieved queue user: {}", sessionId);
        return queueUser;
    }

    @GetMapping("/api/queue")
    @ResponseBody
    public List<QueueUser> getAllQueueUsers() {
        List<QueueUser> queueUsers = queueService.findAllQueueUsers();
        log.info("Retrieved {} queue users", queueUsers.size());
        return queueUsers;
    }

    @PutMapping("/api/queue/{sessionId}")
    @ResponseBody
    public QueueUser updateQueueUser(@PathVariable String sessionId, @RequestBody QueueUser updatedUser) {
        if (sessionId == null || sessionId.isEmpty() || !sessionId.equals(updatedUser.getSessionId())) {
            throw new IllegalArgumentException("SessionId is invalid or does not match request body");
        }
        QueueUser existingUser = queueService.findQueueUserById(sessionId);
        if (existingUser == null) {
            throw new IllegalStateException("Queue user with sessionId " + sessionId + " not found");
        }
        existingUser.setMatched(updatedUser.isMatched());
        existingUser.setJoinedAt(updatedUser.getJoinedAt() != null ? updatedUser.getJoinedAt() : new Date());
        QueueUser savedUser = queueService.saveQueueUser(existingUser);
        log.info("Updated queue user: {}", sessionId);
        return savedUser;
    }

    @DeleteMapping("/api/queue/{sessionId}")
    @ResponseBody
    public void deleteQueueUser(@PathVariable String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            throw new IllegalArgumentException("SessionId is required");
        }
        QueueUser queueUser = queueService.findQueueUserById(sessionId);
        if (queueUser == null) {
            throw new IllegalStateException("Queue user with sessionId " + sessionId + " not found");
        }
        queueService.removeUserFromQueue(sessionId);
        log.info("Deleted queue user: {}", sessionId);
    }
}