package com.example.demo.controller;

import com.example.demo.dto.request.WorkspaceMemberRequest;
import com.example.demo.dto.response.WorkspaceMemberResponse;
import com.example.demo.service.WorkspaceMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workspace-members")
@RequiredArgsConstructor
public class WorkspaceMemberController {

    private final WorkspaceMemberService workspaceMemberService;

    @PostMapping("/invite")
    @PreAuthorize("hasPermission(#request.getWorkspaceId(), 'invite_member')")
    public ResponseEntity<WorkspaceMemberResponse> inviteMember(@RequestBody WorkspaceMemberRequest request) {
        return ResponseEntity.ok(workspaceMemberService.addMember(request));
    }

    @GetMapping("/workspace/{workspaceId}")
    @PreAuthorize("hasPermission(#workspaceId, 'read_workspace_members')")
    public ResponseEntity<List<WorkspaceMemberResponse>> getMembers(@PathVariable Integer workspaceId) {
        return ResponseEntity.ok(workspaceMemberService.getMembersByWorkspaceId(workspaceId));
    }

    @PutMapping("/{id}/role")
    @PreAuthorize("hasPermission(#id, 'update_workspace_member_role')")
    public ResponseEntity<WorkspaceMemberResponse> updateRole(@PathVariable Integer id, @RequestParam Integer newRoleId) {
        return ResponseEntity.ok(workspaceMemberService.updateMemberRole(id, newRoleId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasPermission(#id, 'remove_workspace_member')")
    public ResponseEntity<Void> removeMember(@PathVariable Integer id) {
        workspaceMemberService.removeMember(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/member/{memberId}")
    @PreAuthorize("hasPermission(#memberId, 'read_workspaces_by_member')")
    public ResponseEntity<List<WorkspaceMemberResponse>> getWorkspaces(@PathVariable Integer memberId) {
        return ResponseEntity.ok(workspaceMemberService.getWorkspacesByMemberId(memberId));
    }
}