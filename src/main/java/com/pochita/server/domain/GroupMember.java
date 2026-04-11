package com.pochita.server.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "group_members")
@IdClass(GroupMember.GroupMemberId.class)
public class GroupMember {

    @Id
    @Column(nullable = false)
    private String groupId;

    @Id
    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private long joinedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GroupRole role;

    protected GroupMember() {
    }

    public GroupMember(String groupId, String userId, long joinedAt, GroupRole role) {
        this.groupId = groupId;
        this.userId = userId;
        this.joinedAt = joinedAt;
        this.role = role;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getUserId() {
        return userId;
    }

    public long getJoinedAt() {
        return joinedAt;
    }

    public GroupRole getRole() {
        return role;
    }

    public static class GroupMemberId implements Serializable {
        private String groupId;
        private String userId;

        public GroupMemberId() {
        }

        public GroupMemberId(String groupId, String userId) {
            this.groupId = groupId;
            this.userId = userId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            GroupMemberId that = (GroupMemberId) o;
            return Objects.equals(groupId, that.groupId) && Objects.equals(userId, that.userId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(groupId, userId);
        }
    }
}
