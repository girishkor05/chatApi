package com.example.app.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "queue_users")
public class QueueUser {
    @Id
    private String sessionId;
    private Date joinedAt;
    private boolean matched;
}
