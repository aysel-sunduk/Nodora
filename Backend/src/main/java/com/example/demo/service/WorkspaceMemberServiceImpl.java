package com.example.demo.service;

import com.example.demo.dto.request.WorkspaceMemberRequest;
import com.example.demo.dto.response.WorkspaceMemberResponse;
import com.example.demo.model.members.Member;
import com.example.demo.model.roles.Roles;
import com.example.demo.model.workspace_members.WorkspaceMember;
import com.example.demo.model.workspaces.Workspaces;
import com.example.demo.repository.MemberRepository;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.WorkspaceMemberRepository;
import com.example.demo.repository.WorkspaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WorkspaceMemberServiceImpl implements WorkspaceMemberService {

    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final WorkspaceRepository workspaceRepository;
    private final MemberRepository memberRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public WorkspaceMemberServiceImpl(WorkspaceMemberRepository workspaceMemberRepository,
                                      WorkspaceRepository workspaceRepository,
                                      MemberRepository memberRepository,
                                      RoleRepository roleRepository) {
        this.workspaceMemberRepository = workspaceMemberRepository;
        this.workspaceRepository = workspaceRepository;
        this.memberRepository = memberRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public WorkspaceMemberResponse addMember(WorkspaceMemberRequest request) {
        Workspaces workspace = workspaceRepository.findById(request.getWorkspaceId())
                .orElseThrow(() -> new RuntimeException("Workspace not found"));

        Member member = memberRepository.findByEmail(request.getMemberEmail())
                .orElseThrow(() -> new RuntimeException("Member with email " + request.getMemberEmail() + " not found"));

        // Varsayılan WORKSPACE MEMBER rolünü bul
        Roles defaultMemberRole = roleRepository.findByRoleNameAndScope("MEMBER", "WORKSPACE")
                .orElseThrow(() -> new RuntimeException("Default WORKSPACE MEMBER role not found"));

        // Üyenin zaten workspace'te olup olmadığını kontrol et
        if (workspaceMemberRepository.findByWorkspace_WorkspaceIdAndMember_MemberId(workspace.getWorkspaceId(), member.getMemberId()).isPresent()) {
            throw new RuntimeException("This member is already in the workspace.");
        }

        WorkspaceMember memberEntity = new WorkspaceMember();
        memberEntity.setWorkspace(workspace);
        memberEntity.setMember(member);
        memberEntity.setRole(defaultMemberRole);
        memberEntity.setCreatedAt(LocalDateTime.now());

        WorkspaceMember saved = workspaceMemberRepository.save(memberEntity);

        return toResponse(saved);
    }

    @Override
    public List<WorkspaceMemberResponse> getMembersByWorkspaceId(Integer workspaceId) {
        return workspaceMemberRepository.findByWorkspace_WorkspaceId(workspaceId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public WorkspaceMemberResponse updateMemberRole(Integer workspaceMemberId, Integer newRoleId) {
        WorkspaceMember member = workspaceMemberRepository.findById(workspaceMemberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        Roles role = roleRepository.findById(newRoleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        member.setRole(role);
        WorkspaceMember updated = workspaceMemberRepository.save(member);

        return toResponse(updated);
    }

    @Override
    public void removeMember(Integer workspaceMemberId) {
        workspaceMemberRepository.deleteById(workspaceMemberId);
    }

    @Override
    public List<WorkspaceMemberResponse> getWorkspacesByMemberId(Integer memberId) {
        return workspaceMemberRepository.findByMember_MemberId(memberId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private WorkspaceMemberResponse toResponse(WorkspaceMember member) {
        WorkspaceMemberResponse response = new WorkspaceMemberResponse();
        response.setWorkspaceMemberId(member.getId());
        response.setWorkspaceId(member.getWorkspace().getWorkspaceId());
        response.setWorkspaceName(member.getWorkspace().getWorkspaceName());
        response.setMemberId(member.getMember().getMemberId());
        response.setMemberName(member.getMember().getMemberName());
        response.setMemberEmail(member.getMember().getEmail());
        response.setRoleId(member.getRole().getRoleId());
        response.setRoleName(member.getRole().getRoleName());
        response.setCreatedAt(member.getCreatedAt());
        System.out.println("Oluşturulan WorkspaceMemberResponse: " + response);

        return response;
    }
}