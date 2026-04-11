package com.pochita.server.repository;

import com.pochita.server.domain.GroupMember;
import com.pochita.server.domain.GroupMember.GroupMemberId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupMemberRepository extends JpaRepository<GroupMember, GroupMemberId> {
    List<GroupMember> findByGroupId(String groupId);
    List<GroupMember> findByUserId(String userId);
    boolean existsByGroupIdAndUserId(String groupId, String userId);
    void deleteByGroupIdAndUserId(String groupId, String userId);
}
