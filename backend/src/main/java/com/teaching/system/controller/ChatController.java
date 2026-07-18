package com.teaching.system.controller;

import com.teaching.system.entity.Message;
import com.teaching.system.entity.User;
import com.teaching.system.mapper.MessageMapper;
import com.teaching.system.mapper.UserMapper;
import com.teaching.system.util.R;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {
    private final UserMapper userMapper;
    private final MessageMapper messageMapper;

    @GetMapping("/contacts")
    public R getContacts(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        User me = userMapper.selectById(userId);
        if ("teacher".equals(me.getRole())) return R.ok(userMapper.findAllStudents());
        return R.ok(userMapper.findAllTeachers());
    }

    @GetMapping("/messages/{partnerId}")
    public R getMessages(@PathVariable Long partnerId, Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return R.ok(messageMapper.findConversation(userId, partnerId));
    }

    @PostMapping("/send")
    public R sendMessage(@RequestBody Map<String, Object> body, Authentication auth) {
        Long senderId = (Long) auth.getPrincipal();
        Long receiverId = Long.valueOf(body.get("receiverId").toString());
        String content = (String) body.getOrDefault("content", "");
        Message msg = new Message();
        msg.setSenderId(senderId);
        msg.setReceiverId(receiverId);
        msg.setContent(content);
        messageMapper.insert(msg);
        return R.ok(msg);
    }
}
