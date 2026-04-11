package com.pochita.server.dto;

import com.pochita.server.domain.AuthProvider;
import com.pochita.server.domain.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class AuthDtos {

    private AuthDtos() {
    }

    public record RegisterRequest(
            @Email @NotBlank String email,
            @NotBlank @Size(min = 6) String password,
            @NotBlank String nickname,
            @NotBlank String university,
            @NotBlank String major,
            @NotBlank String year,
            @NotBlank String avatarEmoji
    ) {
    }

    public record LoginRequest(
            @NotBlank String email,
            @NotBlank String password
    ) {
    }

    public record GoogleLoginRequest(
            @NotBlank String email,
            @NotBlank String name,
            @NotBlank String googleId
    ) {
    }

    public record UpdateUserRequest(
            @NotBlank String nickname,
            @NotBlank String university,
            @NotBlank String major,
            @NotBlank String year,
            @NotBlank String avatarEmoji
    ) {
    }

    public record UserResponse(
            String id,
            String email,
            String authProvider,
            String nickname,
            String university,
            String major,
            String year,
            String avatarEmoji,
            long createdAt
    ) {
        public static UserResponse from(User user) {
            return new UserResponse(
                    user.getId(),
                    user.getEmail(),
                    user.getAuthProvider().name().toLowerCase(),
                    user.getNickname(),
                    user.getUniversity(),
                    user.getMajor(),
                    user.getYear(),
                    user.getAvatarEmoji(),
                    user.getCreatedAt()
            );
        }
    }
}
