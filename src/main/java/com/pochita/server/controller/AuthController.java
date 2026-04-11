package com.pochita.server.controller;

import com.pochita.server.dto.AuthDtos.LoginRequest;
import com.pochita.server.dto.AuthDtos.RegisterRequest;
import com.pochita.server.dto.AuthDtos.GoogleLoginRequest;
import com.pochita.server.dto.AuthDtos.UpdateUserRequest;
import com.pochita.server.dto.AuthDtos.UserResponse;
import com.pochita.server.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/auth/register")
    public UserResponse register(@Valid @RequestBody RegisterRequest request) {
        return UserResponse.from(authService.register(request));
    }

    @PostMapping("/auth/login")
    public UserResponse login(@Valid @RequestBody LoginRequest request) {
        return UserResponse.from(authService.login(request));
    }

    @PostMapping("/auth/google-demo")
    public UserResponse googleDemoLogin() {
        return UserResponse.from(authService.ensureGoogleDemoUser());
    }

    @PostMapping("/auth/google")
    public UserResponse googleLogin(@Valid @RequestBody GoogleLoginRequest request) {
        return UserResponse.from(authService.loginWithGoogle(request));
    }

    @GetMapping("/users/{userId}")
    public UserResponse getUser(@PathVariable String userId) {
        return UserResponse.from(authService.getUser(userId));
    }

    @PatchMapping("/users/{userId}")
    public UserResponse updateUser(@PathVariable String userId, @Valid @RequestBody UpdateUserRequest request) {
        return UserResponse.from(authService.updateUserProfile(userId, request));
    }
}
