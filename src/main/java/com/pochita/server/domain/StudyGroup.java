package com.pochita.server.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "study_groups")
public class StudyGroup {

    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(nullable = false)
    private String emoji;

    @Column(nullable = false)
    private String createdBy;

    @Column(nullable = false, unique = true)
    private String inviteCode;

    @Column(nullable = false)
    private long createdAt;

    @Column(nullable = false)
    private boolean isPublic;

    @Column(nullable = false)
    private boolean requiresPassword;

    private String passwordHash;

    protected StudyGroup() {
    }

    public StudyGroup(
            String id,
            String name,
            String description,
            String emoji,
            String createdBy,
            String inviteCode,
            long createdAt,
            boolean isPublic,
            boolean requiresPassword,
            String passwordHash
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.emoji = emoji;
        this.createdBy = createdBy;
        this.inviteCode = inviteCode;
        this.createdAt = createdAt;
        this.isPublic = isPublic;
        this.requiresPassword = requiresPassword;
        this.passwordHash = passwordHash;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getEmoji() {
        return emoji;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public boolean isRequiresPassword() {
        return requiresPassword;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void regenerateInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }
}
