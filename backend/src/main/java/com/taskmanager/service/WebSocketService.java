package com.taskmanager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebSocketService {

    private final SimpMessagingTemplate template;

    public void broadcast(String type, Object payload) {
        template.convertAndSend("/topic/tasks", new WsMessage(type, payload));
    }

    public record WsMessage(String type, Object payload) {}
}
