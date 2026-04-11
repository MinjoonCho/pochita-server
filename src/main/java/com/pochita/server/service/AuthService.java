package com.pochita.server.service;

import com.pochita.server.common.ApiException;
import com.pochita.server.common.IdGenerator;
import com.pochita.server.common.PasswordHasher;
import com.pochita.server.domain.AuthProvider;
import com.pochita.server.domain.User;
import com.pochita.server.dto.AuthDtos.GoogleLoginRequest;
import com.pochita.server.dto.AuthDtos.LoginRequest;
import com.pochita.server.dto.AuthDtos.RegisterRequest;
import com.pochita.server.dto.AuthDtos.UpdateUserRequest;
import java.util.List;
import com.pochita.server.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(RegisterRequest request) {
        String email = normalizeEmail(request.email());
        if (userRepository.existsByEmail(email)) {
            throw new ApiException(HttpStatus.CONFLICT, "이미 가입된 이메일입니다.");
        }

        User user = new User(
                IdGenerator.newId(),
                email,
                PasswordHasher.hash(request.password()),
                AuthProvider.CREDENTIALS,
                request.nickname().trim(),
                request.university().trim(),
                request.major().trim(),
                request.year().trim(),
                request.avatarEmoji().trim(),
                System.currentTimeMillis()
        );

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User login(LoginRequest request) {
        String email = normalizeEmail(request.email());

        if (("test".equals(email) || "test@test.com".equals(email)) && "123456".equals(request.password())) {
            return ensureTestUser();
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다."));

        if (!user.getPasswordHash().equals(PasswordHasher.hash(request.password()))) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        return user;
    }

    public User ensureGoogleDemoUser() {
        return userRepository.findByEmail("google.demo@pochita.app")
                .orElseGet(() -> userRepository.save(new User(
                        "google-demo",
                        "google.demo@pochita.app",
                        PasswordHasher.hash("google-demo"),
                        AuthProvider.GOOGLE,
                        "구글포치",
                        "포치타대학교",
                        "딴짓공학과",
                        "4학년",
                        "🦊",
                        System.currentTimeMillis()
                )));
    }

    public User loginWithGoogle(GoogleLoginRequest request) {
        String email = normalizeEmail(request.email());
        String nickname = request.name().trim();
        String avatarEmoji = pickGoogleAvatar(email);

        return userRepository.findByEmail(email)
                .map(existingUser -> {
                    existingUser.syncGoogleProfile(nickname, avatarEmoji);
                    return userRepository.save(existingUser);
                })
                .orElseGet(() -> userRepository.save(new User(
                        IdGenerator.newId(),
                        email,
                        PasswordHasher.hash("google:" + request.googleId().trim()),
                        AuthProvider.GOOGLE,
                        nickname,
                        "학교 미설정",
                        "전공 미설정",
                        "학년 미설정",
                        avatarEmoji,
                        System.currentTimeMillis()
                )));
    }

    public User getUser(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));
    }

    public User updateUserProfile(String userId, UpdateUserRequest request) {
        User user = getUser(userId);
        user.updateProfile(
                request.nickname().trim(),
                request.university().trim(),
                request.major().trim(),
                request.year().trim(),
                request.avatarEmoji().trim()
        );
        return userRepository.save(user);
    }

    private User ensureTestUser() {
        return userRepository.findByEmail("test@test.com")
                .orElseGet(() -> userRepository.save(new User(
                        "test",
                        "test@test.com",
                        PasswordHasher.hash("123456"),
                        AuthProvider.CREDENTIALS,
                        "테스트계정",
                        "포치타대학교",
                        "컴퓨터공학과",
                        "4학년",
                        "🦊",
                        System.currentTimeMillis()
                )));
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }

    private String pickGoogleAvatar(String email) {
        List<String> avatars = List.of("🦊", "😎", "🤖", "🐣", "🌟", "🐱", "🚀", "🔥");
        int index = Math.abs(email.hashCode()) % avatars.size();
        return avatars.get(index);
    }
}
