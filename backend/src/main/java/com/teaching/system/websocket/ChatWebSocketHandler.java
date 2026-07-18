package com.teaching.system.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teaching.system.entity.Message;
import com.teaching.system.mapper.MessageMapper;
import com.teaching.system.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final JwtUtil jwtUtil;
    private final MessageMapper messageMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // userId -> session
    private static final Map<Long, WebSocketSession> onlineUsers = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        Long userId = getUserId(session);
        if (userId != null) {
            onlineUsers.put(userId, session);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws IOException {
        Long senderId = getUserId(session);
        if (senderId == null) return;

        Map<String, Object> payload = objectMapper.readValue(textMessage.getPayload(), Map.class);
        Long receiverId = Long.valueOf(payload.get("receiverId").toString());
        String content = (String) payload.getOrDefault("content", "");
        String filePath = (String) payload.getOrDefault("filePath", "");
        String fileName = (String) payload.getOrDefault("fileName", "");
        String fileType = (String) payload.getOrDefault("fileType", "");

        // 存数据库
        Message msg = new Message();
        msg.setSenderId(senderId);
        msg.setReceiverId(receiverId);
        msg.setContent(content);
        msg.setFilePath(filePath);
        msg.setFileName(fileName);
        msg.setFileType(fileType);
        messageMapper.insert(msg);

        // 推送给对方（如果在线）
        WebSocketSession target = onlineUsers.get(receiverId);
        if (target != null && target.isOpen()) {
            target.sendMessage(new TextMessage(objectMapper.writeValueAsString(msg)));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Long userId = getUserId(session);
        if (userId != null) onlineUsers.remove(userId);
    }

    private Long getUserId(WebSocketSession session) {
        URI uri = session.getUri();
        if (uri == null) return null;
        String query = uri.getQuery();
        if (query == null) return null;
        for (String param : query.split("&")) {
            String[] kv = param.split("=");
            if (kv.length == 2 && "token".equals(kv[0])) {
                try { return jwtUtil.getUserId(kv[1]); } catch (Exception ignored) {}
            }
        }
        return null;
    }
}
