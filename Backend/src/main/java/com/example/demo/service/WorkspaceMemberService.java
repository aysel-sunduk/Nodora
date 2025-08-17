package com.example.demo.service;

import com.example.demo.dto.request.WorkspaceMemberRequest;
import com.example.demo.dto.response.WorkspaceMemberResponse;
import java.util.List;

public interface WorkspaceMemberService {
    WorkspaceMemberResponse addMember(WorkspaceMemberRequest request);
    List<WorkspaceMemberResponse> getMembersByWorkspaceId(Integer workspaceId);
    WorkspaceMemberResponse updateMemberRole(Integer workspaceMemberId, Integer newRoleId);
    void removeMember(Integer workspaceMemberId);
    List<WorkspaceMemberResponse> getWorkspacesByMemberId(Integer memberId);
}