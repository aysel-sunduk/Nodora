package com.example.demo.service;

import com.example.demo.dto.request.WorkspaceRequest;
import com.example.demo.dto.response.WorkspaceResponse;
import com.example.demo.model.members.Member;
import com.example.demo.model.roles.Roles;
import com.example.demo.model.workspace_members.WorkspaceMember;
import com.example.demo.model.workspaces.Workspaces;
import com.example.demo.repository.MemberRepository;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.WorkspaceMemberRepository;
import com.example.demo.repository.WorkspaceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkspaceServiceImpl implements WorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final RoleRepository roleRepository;
    private final MemberRepository memberRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;

    @Override
    @Transactional
    public WorkspaceResponse createWorkspace(WorkspaceRequest request) {
        Integer currentMemberId = getCurrentMemberId();
        Member currentMember = memberRepository.findById(currentMemberId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        Workspaces newWorkspace = new Workspaces();
        newWorkspace.setWorkspaceName(request.getWorkspaceName());
        newWorkspace.setMemberId(currentMemberId);
        Workspaces savedWorkspace = workspaceRepository.save(newWorkspace);

        Roles ownerRole = roleRepository.findByRoleNameAndScope("OWNER", "WORKSPACE")
                .orElseThrow(() -> new RuntimeException("WORKSPACE OWNER rolü bulunamadı"));

        WorkspaceMember memberEntity = new WorkspaceMember();
        memberEntity.setWorkspace(savedWorkspace);
        memberEntity.setMember(currentMember);
        memberEntity.setRole(ownerRole);
        workspaceMemberRepository.save(memberEntity);

        // <<< HATA AYIKLAMA İÇİN EKLENEN SATIRLAR >>>
        System.out.println("BACKEND DEBUG: OWNER Role ID is " + ownerRole.getRoleId());
        System.out.println("BACKEND DEBUG: OWNER Role Name is " + ownerRole.getRoleName());
        // <<< HATA AYIKLAMA İÇİN EKLENEN SATIRLAR >>>

        WorkspaceResponse response = new WorkspaceResponse();
        response.setWorkspaceId(savedWorkspace.getWorkspaceId());
        response.setMemberId(currentMemberId);
        response.setWorkspaceName(savedWorkspace.getWorkspaceName());
        response.setRoleId(ownerRole.getRoleId());
        response.setRoleName(ownerRole.getRoleName());
        return response;
    }
    @Override
    @Transactional
    public void deleteWorkspace(Integer workspaceId) {
        // Workspace var mı kontrolü
        Workspaces workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new RuntimeException("Workspace bulunamadı"));

        // Workspace'e ait tüm WorkspaceMember ilişkilerini sil
        workspaceMemberRepository.deleteByWorkspace_WorkspaceId(workspaceId);

        // Workspace'i sil
        workspaceRepository.delete(workspace);
    }

    @Override
    public List<WorkspaceResponse> getWorkspacesForCurrentUser() {
        Integer currentMemberId = getCurrentMemberId();
        return getWorkspacesByMemberId(currentMemberId);
    }

    @Override
    public List<WorkspaceResponse> getWorkspacesByMemberId(Integer memberId) {
        List<WorkspaceMember> workspaceMembers = workspaceMemberRepository.findByMember_MemberId(memberId);

        return workspaceMembers.stream()
                .filter(wm -> wm.getWorkspace() != null && wm.getRole() != null)
                .map(wm -> {
                    WorkspaceResponse response = new WorkspaceResponse();
                    response.setWorkspaceId(wm.getWorkspace().getWorkspaceId());
                    response.setWorkspaceName(wm.getWorkspace().getWorkspaceName());
                    response.setMemberId(wm.getMember().getMemberId());
                    response.setRoleId(wm.getRole().getRoleId());
                    response.setRoleName(wm.getRole().getRoleName());
                    return response;
                })
                .collect(Collectors.toList());
    }

    private Integer getCurrentMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof UserDetails)) {
            throw new RuntimeException("Kullanıcı oturum açmamış veya kimlik doğrulama bilgisi eksik!");
        }
        String email = authentication.getName();
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
        return member.getMemberId();
    }
}
