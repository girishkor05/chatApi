package com.example.app.repositories;

import com.example.app.models.ActiveSession;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActiveSessionRepository extends MongoRepository<ActiveSession, String> {
    List<ActiveSession> findByUserSessionId1OrUserSessionId2(String sessionId1, String sessionId2);

    default void deleteByUserSessionId1OrUserSessionId2(String ignoredSessionId1, String ignoredSessionId2) {

    }
}
