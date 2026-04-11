package com.pochita.server.service;

import com.pochita.server.common.ApiException;
import com.pochita.server.common.IdGenerator;
import com.pochita.server.common.PasswordHasher;
import com.pochita.server.domain.GroupMember;
import com.pochita.server.domain.GroupRole;
import com.pochita.server.domain.StudyGroup;
import com.pochita.server.domain.User;
import com.pochita.server.dto.GroupDtos.CreateGroupRequest;
import com.pochita.server.dto.GroupDtos.GroupDetailResponse;
import com.pochita.server.dto.GroupDtos.GroupMemberResponse;
import com.pochita.server.dto.GroupDtos.GroupResponse;
import com.pochita.server.dto.GroupDtos.JoinGroupRequest;
import com.pochita.server.dto.GroupDtos.RemoveGroupMemberRequest;
import com.pochita.server.repository.GroupMemberRepository;
import com.pochita.server.repository.StudyGroupRepository;
import com.pochita.server.repository.UserRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class GroupService {

    private final StudyGroupRepository groupRepository;
    private final GroupMemberRepository memberRepository;
    private final UserRepository userRepository;
    private final SessionService sessionService;

    public GroupService(
            StudyGroupRepository groupRepository,
            GroupMemberRepository memberRepository,
            UserRepository userRepository,
            SessionService sessionService
    ) {
        this.groupRepository = groupRepository;
        this.memberRepository = memberRepository;
        this.userRepository = userRepository;
        this.sessionService = sessionService;
    }

    public GroupResponse createGroup(CreateGroupRequest request) {
        requireUser(request.createdBy());

        String trimmedPassword = request.password() == null ? "" : request.password().trim();
        StudyGroup group = new StudyGroup(
                IdGenerator.newId(),
                request.name().trim(),
                request.description() == null ? "" : request.description().trim(),
                request.emoji().trim(),
                request.createdBy().trim(),
                IdGenerator.newInviteCode(),
                System.currentTimeMillis(),
                request.isPublic(),
                !trimmedPassword.isEmpty(),
                trimmedPassword.isEmpty() ? null : PasswordHasher.hash(trimmedPassword)
        );

        groupRepository.save(group);
        memberRepository.save(new GroupMember(group.getId(), request.createdBy().trim(), System.currentTimeMillis(), GroupRole.OWNER));
        return toResponse(group);
    }

    public GroupResponse joinGroup(JoinGroupRequest request) {
        StudyGroup group = groupRepository.findByInviteCode(request.inviteCode().trim().toUpperCase())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "유효하지 않은 초대 코드예요."));

        requireUser(request.userId());

        if (memberRepository.existsByGroupIdAndUserId(group.getId(), request.userId().trim())) {
            throw new ApiException(HttpStatus.CONFLICT, "이미 가입된 그룹이에요.");
        }

        validatePassword(group, request.password());
        memberRepository.save(new GroupMember(group.getId(), request.userId().trim(), System.currentTimeMillis(), GroupRole.MEMBER));
        return toResponse(group);
    }

    @Transactional(readOnly = true)
    public List<GroupResponse> getGroups(String userId) {
        if (userId == null || userId.isBlank()) {
            return groupRepository.findAll().stream().map(this::toResponse).toList();
        }

        List<String> groupIds = memberRepository.findByUserId(userId).stream()
                .map(GroupMember::getGroupId)
                .toList();

        return groupRepository.findAllById(groupIds).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public GroupDetailResponse getGroupDetail(String groupId) {
        StudyGroup group = getGroupEntity(groupId);
        List<GroupMemberResponse> members = getGroupMembers(groupId);
        return new GroupDetailResponse(toResponse(group), members);
    }

    @Transactional(readOnly = true)
    public List<GroupMemberResponse> getGroupMembers(String groupId) {
        getGroupEntity(groupId);
        return memberRepository.findByGroupId(groupId).stream()
                .map(member -> {
                    User user = getUserEntity(member.getUserId());
                    long totalSec = sumCompletedSessionSeconds(sessionService.getUserSessions(member.getUserId()));
                    long todaySec = sumCompletedSessionSeconds(sessionService.getTodaySessions(member.getUserId()));
                    return GroupMemberResponse.from(member, user, todaySec, totalSec);
                })
                .toList();
    }

    public GroupResponse regenerateInviteCode(String groupId) {
        StudyGroup group = getGroupEntity(groupId);
        group.regenerateInviteCode(IdGenerator.newInviteCode());
        return toResponse(groupRepository.save(group));
    }

    public GroupDetailResponse removeMember(String groupId, RemoveGroupMemberRequest request) {
        StudyGroup group = getGroupEntity(groupId);
        GroupMember actor = findMembership(groupId, request.actorUserId());
        GroupMember target = findMembership(groupId, request.targetUserId());

        if (actor.getRole() != GroupRole.OWNER) {
            throw new ApiException(HttpStatus.FORBIDDEN, "그룹장만 멤버를 추방할 수 있어요.");
        }

        if (target.getRole() == GroupRole.OWNER) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "그룹장은 추방할 수 없어요.");
        }

        memberRepository.deleteByGroupIdAndUserId(groupId, request.targetUserId().trim());
        return getGroupDetail(groupId);
    }

    @Transactional(readOnly = true)
    public StudyGroup getGroupEntity(String groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "그룹을 찾을 수 없습니다."));
    }

    @Transactional(readOnly = true)
    public List<GroupMember> getMembers(String groupId) {
        return memberRepository.findByGroupId(groupId);
    }

    private GroupMember findMembership(String groupId, String userId) {
        return memberRepository.findByGroupId(groupId).stream()
                .filter(member -> member.getUserId().equals(userId.trim()))
                .findFirst()
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "그룹 멤버를 찾을 수 없습니다."));
    }

    private GroupResponse toResponse(StudyGroup group) {
        int memberCount = memberRepository.findByGroupId(group.getId()).size();
        return GroupResponse.from(group, memberCount);
    }

    private void validatePassword(StudyGroup group, String password) {
        if (!group.isRequiresPassword()) {
            return;
        }

        String supplied = password == null ? "" : password.trim();
        if (supplied.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "그룹 비밀번호를 입력해주세요.");
        }

        if (!PasswordHasher.hash(supplied).equals(group.getPasswordHash())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "그룹 비밀번호가 올바르지 않아요.");
        }
    }

    private User requireUser(String userId) {
        return getUserEntity(userId);
    }

    private User getUserEntity(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));
    }

    private long sumCompletedSessionSeconds(List<com.pochita.server.domain.DistractionSession> sessions) {
        return sessions.stream()
                .map(com.pochita.server.domain.DistractionSession::getDuration)
                .filter(java.util.Objects::nonNull)
                .mapToLong(Long::longValue)
                .sum();
    }
}
