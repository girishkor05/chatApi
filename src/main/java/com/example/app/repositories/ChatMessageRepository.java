package com.example.app.repositories;

import com.example.app.models.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
    List<ChatMessage> findByFromSessionIdAndToSessionId(String fromSessionId, String toSessionId);
}
