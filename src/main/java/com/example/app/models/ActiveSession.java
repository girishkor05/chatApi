package com.example.app.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "active_sessions")
public class ActiveSession {
    @Id
    private String id;
    private String userSessionId1;
    private String userSessionId2;
    private Date startedAt;
    private boolean active;
}
