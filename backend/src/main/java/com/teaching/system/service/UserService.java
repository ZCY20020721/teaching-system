package com.teaching.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.teaching.system.dto.LoginResponse;
import com.teaching.system.dto.RegisterRequest;
import com.teaching.system.entity.User;
import com.teaching.system.mapper.UserMapper;
import com.teaching.system.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public String register(RegisterRequest req) {
        if (req.getPassword().length() < 6) return "密码至少6位";
        if (!req.getPassword().matches(".*\\d.*")) return "密码需包含数字";
        if (!req.getPassword().matches(".*[a-zA-Z].*")) return "密码需包含字母";

        User exist = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, req.getUsername()));
        if (exist != null) return "用户名已存在";

        User user = new User();
        user.setUsername(req.getUsername());
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        user.setRole(req.getRole());
        userMapper.insert(user);
        return null; // null means success
    }

    public LoginResponse login(String username, String password) {
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (user == null) return null;
        if (!passwordEncoder.matches(password, user.getPasswordHash())) return null;

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        return new LoginResponse(token, user.getId(), user.getUsername(), user.getRole());
    }

    public User getById(Long id) {
        return userMapper.selectById(id);
    }
}
