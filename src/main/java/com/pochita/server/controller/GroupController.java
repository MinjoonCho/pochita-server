package com.pochita.server.controller;

import com.pochita.server.dto.GroupDtos.CreateGroupRequest;
import com.pochita.server.dto.GroupDtos.GroupDetailResponse;
import com.pochita.server.dto.GroupDtos.GroupMemberResponse;
import com.pochita.server.dto.GroupDtos.GroupResponse;
import com.pochita.server.dto.GroupDtos.JoinGroupRequest;
import com.pochita.server.dto.GroupDtos.RemoveGroupMemberRequest;
import com.pochita.server.service.GroupService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @PostMapping
    public GroupResponse createGroup(@Valid @RequestBody CreateGroupRequest request) {
        return groupService.createGroup(request);
    }

    @PostMapping("/join")
    public GroupResponse joinGroup(@Valid @RequestBody JoinGroupRequest request) {
        return groupService.joinGroup(request);
    }

    @GetMapping
    public List<GroupResponse> getGroups(@RequestParam(required = false) String userId) {
        return groupService.getGroups(userId);
    }

    @GetMapping("/{groupId}")
    public GroupDetailResponse getGroup(@PathVariable String groupId) {
        return groupService.getGroupDetail(groupId);
    }

    @GetMapping("/{groupId}/members")
    public List<GroupMemberResponse> getGroupMembers(@PathVariable String groupId) {
        return groupService.getGroupMembers(groupId);
    }

    @PostMapping("/{groupId}/invite-code")
    public GroupResponse regenerateInviteCode(@PathVariable String groupId) {
        return groupService.regenerateInviteCode(groupId);
    }

    @PostMapping("/{groupId}/remove-member")
    public GroupDetailResponse removeMember(@PathVariable String groupId, @Valid @RequestBody RemoveGroupMemberRequest request) {
        return groupService.removeMember(groupId, request);
    }
}
