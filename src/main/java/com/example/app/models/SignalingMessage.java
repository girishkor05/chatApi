package com.example.app.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignalingMessage {
    private String type;
    private String sessionId;
    private String targetSessionId;
    private Object sdp;
    private Object candidate;
    private String content;

    public void setFromSessionId(String sessionId) {
    }

    public void setToSessionId(String sessionId) {
    }
}
