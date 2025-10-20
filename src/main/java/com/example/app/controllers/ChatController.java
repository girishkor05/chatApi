package com.example.app.controllers;

import com.example.app.models.ChatMessage;
import com.example.app.models.SignalingMessage;
import com.example.app.services.ChatService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class ChatController {

    private final ChatService chatService;
    private static final Logger log = LoggerFactory.getLogger(ChatController.class);

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    // Existing WebSocket handler for sending messages
    @MessageMapping("/session.message")
    public void sendMessage(SignalingMessage message) {
        if (message == null || message.getSessionId() == null || message.getTargetSessionId() == null || message.getContent() == null) {
            log.error("Invalid chat message: missing required fields");
            return;
        }
        log.info("Chat message from {} to {}", message.getSessionId(), message.getTargetSessionId());
        chatService.sendMessage(message);
    }

    // Existing REST endpoint for chat history
    @GetMapping("/api/chat/history")
    @ResponseBody
    public List<ChatMessage> getChatHistory(@RequestParam String sessionId1, @RequestParam String sessionId2) {
        if (sessionId1 == null || sessionId2 == null || sessionId1.isEmpty() || sessionId2.isEmpty()) {
            throw new IllegalArgumentException("Both session IDs are required");
        }
        return chatService.getMessageHistory(sessionId1, sessionId2);
    }

    // CRUD: Create a chat message
    @PostMapping("/api/chat")
    @ResponseBody
    public ChatMessage createChatMessage(@RequestBody ChatMessage chatMessage) {
        if (chatMessage == null || chatMessage.getFromSessionId() == null || chatMessage.getToSessionId() == null || chatMessage.getContent() == null) {
            throw new IllegalArgumentException("FromSessionId, ToSessionId, and Content are required");
        }
        chatMessage.setId(UUID.randomUUID().toString());
        chatMessage.setSentAt(new Date());
        ChatMessage savedMessage = chatService.saveChatMessage(chatMessage);
        SignalingMessage signalingMessage = new SignalingMessage();
        signalingMessage.setType("CHAT_MESSAGE");
        signalingMessage.setSessionId(chatMessage.getFromSessionId());
        signalingMessage.setTargetSessionId(chatMessage.getToSessionId());
        signalingMessage.setContent(chatMessage.getContent());
        chatService.sendMessage(signalingMessage); // Notify recipient via WebSocket
        log.info("Created chat message: {}", savedMessage.getId());
        return savedMessage;
    }

    // CRUD: Read a chat message by ID
    @GetMapping("/api/chat/{id}")
    @ResponseBody
    public ChatMessage getChatMessage(@PathVariable String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Message ID is required");
        }
        ChatMessage chatMessage = chatService.findChatMessageById(id);
        if (chatMessage == null) {
            throw new IllegalStateException("Chat message with ID " + id + " not found");
        }
        log.info("Retrieved chat message: {}", id);
        return chatMessage;
    }

    // CRUD: Read all chat messages
    @GetMapping("/api/chat")
    @ResponseBody
    public List<ChatMessage> getAllChatMessages() {
        List<ChatMessage> chatMessages = chatService.findAllChatMessages();
        log.info("Retrieved {} chat messages", chatMessages.size());
        return chatMessages;
    }

    // CRUD: Update a chat message
    @PutMapping("/api/chat/{id}")
    @ResponseBody
    public ChatMessage updateChatMessage(@PathVariable String id, @RequestBody ChatMessage updatedMessage) {
        if (id == null || id.isEmpty() || !id.equals(updatedMessage.getId())) {
            throw new IllegalArgumentException("Message ID is invalid or does not match request body");
        }
        ChatMessage existingMessage = chatService.findChatMessageById(id);
        if (existingMessage == null) {
            throw new IllegalStateException("Chat message with ID " + id + " not found");
        }
        existingMessage.setFromSessionId(updatedMessage.getFromSessionId() != null ? updatedMessage.getFromSessionId() : existingMessage.getFromSessionId());
        existingMessage.setToSessionId(updatedMessage.getToSessionId() != null ? updatedMessage.getToSessionId() : existingMessage.getToSessionId());
        existingMessage.setContent(updatedMessage.getContent() != null ? updatedMessage.getContent() : existingMessage.getContent());
        existingMessage.setSentAt(updatedMessage.getSentAt() != null ? updatedMessage.getSentAt() : existingMessage.getSentAt());
        ChatMessage savedMessage = chatService.saveChatMessage(existingMessage);
        log.info("Updated chat message: {}", id);
        return savedMessage;
    }

    // CRUD: Delete a chat message
    @DeleteMapping("/api/chat/{id}")
    @ResponseBody
    public void deleteChatMessage(@PathVariable String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Message ID is required");
        }
        ChatMessage chatMessage = chatService.findChatMessageById(id);
        if (chatMessage == null) {
            throw new IllegalStateException("Chat message with ID " + id + " not found");
        }
        chatService.deleteChatMessage(id);
        log.info("Deleted chat message: {}", id);
    }
}