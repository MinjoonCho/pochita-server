package com.pochita.server.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {

    @Id
    private String id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthProvider authProvider;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String university;

    @Column(nullable = false)
    private String major;

    @Column(name = "academic_year", nullable = false)
    private String year;

    @Column(nullable = false)
    private String avatarEmoji;

    @Column(nullable = false)
    private long createdAt;

    protected User() {
    }

    public User(
            String id,
            String email,
            String passwordHash,
            AuthProvider authProvider,
            String nickname,
            String university,
            String major,
            String year,
            String avatarEmoji,
            long createdAt
    ) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.authProvider = authProvider;
        this.nickname = nickname;
        this.university = university;
        this.major = major;
        this.year = year;
        this.avatarEmoji = avatarEmoji;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public AuthProvider getAuthProvider() {
        return authProvider;
    }

    public String getNickname() {
        return nickname;
    }

    public String getUniversity() {
        return university;
    }

    public String getMajor() {
        return major;
    }

    public String getYear() {
        return year;
    }

    public String getAvatarEmoji() {
        return avatarEmoji;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void updateProfile(String nickname, String university, String major, String year, String avatarEmoji) {
        this.nickname = nickname;
        this.university = university;
        this.major = major;
        this.year = year;
        this.avatarEmoji = avatarEmoji;
    }

    public void syncGoogleProfile(String nickname, String avatarEmoji) {
        this.nickname = nickname;
        this.avatarEmoji = avatarEmoji;
    }
}
