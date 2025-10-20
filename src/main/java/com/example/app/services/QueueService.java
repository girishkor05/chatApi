package com.example.app.services;

import com.example.app.models.ActiveSession;
import com.example.app.models.QueueUser;
import com.example.app.models.SignalingMessage;
import com.example.app.repositories.ActiveSessionRepository;
import com.example.app.repositories.QueueUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class QueueService {

    private final QueueUserRepository queueUserRepository;

    private final ActiveSessionRepository activeSessionRepository;

    private final SimpMessagingTemplate messagingTemplate;

    public QueueService(QueueUserRepository queueUserRepository, ActiveSessionRepository activeSessionRepository, SimpMessagingTemplate messagingTemplate) {
        this.queueUserRepository = queueUserRepository;
        this.activeSessionRepository = activeSessionRepository;
        this.messagingTemplate = messagingTemplate;
    }

    public void addUserToQueue(String sessionId) {
        QueueUser queueUser = new QueueUser();
        queueUser.setSessionId(sessionId);
        queueUser.setJoinedAt(new Date());
        queueUser.setMatched(false);
        queueUserRepository.save(queueUser);

        matchUsers();
    }

    public void removeUserFromQueue(String sessionId) {
        queueUserRepository.deleteById(sessionId);
    }

    public void matchUsers() {
        List<QueueUser> waitingUsers = queueUserRepository.findByMatchedFalseOrderByJoinedAt();

        if (waitingUsers.size() >= 2) {
            QueueUser user1 = waitingUsers.get(0);
            QueueUser user2 = waitingUsers.get(1);

            // Mark users as matched
            user1.setMatched(true);
            user2.setMatched(true);
            queueUserRepository.save(user1);
            queueUserRepository.save(user2);

            ActiveSession session = new ActiveSession();
            session.setId(UUID.randomUUID().toString());
            session.setUserSessionId1(user1.getSessionId());
            session.setUserSessionId2(user2.getSessionId());
            session.setStartedAt(new Date());
            session.setActive(true);
            activeSessionRepository.save(session);

            // Notify users about match
            SignalingMessage message1 = new SignalingMessage();
            message1.setType("MATCH_FOUND");
            message1.setSessionId(user1.getSessionId());
            message1.setTargetSessionId(user2.getSessionId());
            messagingTemplate.convertAndSend("/topic/session/" + user1.getSessionId(), message1);

            SignalingMessage message2 = new SignalingMessage();
            message2.setType("MATCH_FOUND");
            message2.setSessionId(user2.getSessionId());
            message2.setTargetSessionId(user1.getSessionId());
            messagingTemplate.convertAndSend("/topic/session/" + user2.getSessionId(), message2);

            // Remove matched users from queue
            queueUserRepository.deleteById(user1.getSessionId());
            queueUserRepository.deleteById(user2.getSessionId());
        }
    }

    // CRUD Support
    public QueueUser saveQueueUser(QueueUser queueUser) {
        return queueUserRepository.save(queueUser);
    }

    public QueueUser findQueueUserById(String sessionId) {
        return queueUserRepository.findById(sessionId).orElse(null);
    }

    public List<QueueUser> findAllQueueUsers() {
        return queueUserRepository.findAll();
    }

    public void deleteQueueUser(String sessionId) {
        queueUserRepository.deleteById(sessionId);
    }

    public List<QueueUser> findByMatchedFalse() {
        return queueUserRepository.findByMatchedFalse();
    }
}
