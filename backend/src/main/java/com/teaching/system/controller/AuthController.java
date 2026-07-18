package com.teaching.system.controller;

import com.teaching.system.dto.LoginRequest;
import com.teaching.system.dto.LoginResponse;
import com.teaching.system.dto.RegisterRequest;
import com.teaching.system.service.UserService;
import com.teaching.system.util.R;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public R register(@Valid @RequestBody RegisterRequest req) {
        String err = userService.register(req);
        if (err != null) return R.error(err);
        return R.ok("注册成功");
    }

    @PostMapping("/login")
    public R login(@Valid @RequestBody LoginRequest req) {
        LoginResponse resp = userService.login(req.getUsername(), req.getPassword());
        if (resp == null) return R.error("用户名或密码错误");
        return R.ok(resp);
    }
}
