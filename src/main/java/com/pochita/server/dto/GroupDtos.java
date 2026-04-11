package com.pochita.server.dto;

import com.pochita.server.domain.GroupMember;
import com.pochita.server.domain.StudyGroup;
import com.pochita.server.domain.User;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

public final class GroupDtos {

    private GroupDtos() {
    }

    public record CreateGroupRequest(
            @NotBlank String name,
            String description,
            @NotBlank String emoji,
            @NotBlank String createdBy,
            boolean isPublic,
            String password
    ) {
    }

    public record JoinGroupRequest(
            @NotBlank String inviteCode,
            @NotBlank String userId,
            String password
    ) {
    }

    public record RemoveGroupMemberRequest(
            @NotBlank String actorUserId,
            @NotBlank String targetUserId
    ) {
    }

    public record GroupResponse(
            String id,
            String name,
            String description,
            String emoji,
            String createdBy,
            String inviteCode,
            long createdAt,
            boolean isPublic,
            boolean requiresPassword,
            int memberCount
    ) {
        public static GroupResponse from(StudyGroup group, int memberCount) {
            return new GroupResponse(
                    group.getId(),
                    group.getName(),
                    group.getDescription(),
                    group.getEmoji(),
                    group.getCreatedBy(),
                    group.getInviteCode(),
                    group.getCreatedAt(),
                    group.isPublic(),
                    group.isRequiresPassword(),
                    memberCount
            );
        }
    }

    public record GroupMemberResponse(
            String userId,
            String nickname,
            String university,
            String major,
            String avatarEmoji,
            String role,
            long joinedAt,
            long todaySec,
            long totalSec
    ) {
        public static GroupMemberResponse from(GroupMember member, User user, long todaySec, long totalSec) {
            return new GroupMemberResponse(
                    user.getId(),
                    user.getNickname(),
                    user.getUniversity(),
                    user.getMajor(),
                    user.getAvatarEmoji(),
                    member.getRole().name().toLowerCase(),
                    member.getJoinedAt(),
                    todaySec,
                    totalSec
            );
        }
    }

    public record GroupDetailResponse(
            GroupResponse group,
            List<GroupMemberResponse> members
    ) {
    }
}
