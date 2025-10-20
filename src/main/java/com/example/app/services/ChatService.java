package com.example.app.services;

import com.example.app.models.ChatMessage;
import com.example.app.models.SignalingMessage;
import com.example.app.repositories.ChatMessageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;

    private final SimpMessagingTemplate messagingTemplate;

    public ChatService(ChatMessageRepository chatMessageRepository, SimpMessagingTemplate messagingTemplate) {
        this.chatMessageRepository = chatMessageRepository;
        this.messagingTemplate = messagingTemplate;
    }

    public void sendMessage(SignalingMessage message) {
        // Save message to database
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setId(UUID.randomUUID().toString());
        chatMessage.setFromSessionId(message.getSessionId());
        chatMessage.setToSessionId(message.getTargetSessionId());
        chatMessage.setContent(message.getContent());
        chatMessage.setSentAt(new Date());
        chatMessageRepository.save(chatMessage);

        // Forward message to recipient
        messagingTemplate.convertAndSend("/topic/session/" + message.getTargetSessionId(), message);
    }

    public List<ChatMessage> getMessageHistory(String sessionId1, String sessionId2) {
        List<ChatMessage> sentMessages = chatMessageRepository.findByFromSessionIdAndToSessionId(sessionId1, sessionId2);
        List<ChatMessage> receivedMessages = chatMessageRepository.findByFromSessionIdAndToSessionId(sessionId2, sessionId1);

        List<ChatMessage> allMessages = new ArrayList<>();
        allMessages.addAll(sentMessages);
        allMessages.addAll(receivedMessages);

        // Sort by timestamp
        allMessages.sort(Comparator.comparing(ChatMessage::getSentAt));

        return allMessages;
    }

    // CRUD Support
    public ChatMessage saveChatMessage(ChatMessage chatMessage) {
        return chatMessageRepository.save(chatMessage);
    }

    public ChatMessage findChatMessageById(String id) {
        return chatMessageRepository.findById(id).orElse(null);
    }

    public List<ChatMessage> findAllChatMessages() {
        return chatMessageRepository.findAll();
    }

    public void deleteChatMessage(String id) {
        chatMessageRepository.deleteById(id);
    }
}
