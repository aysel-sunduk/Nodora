package com.example.demo.service;

import com.example.demo.dto.response.AdminDashboardResponse;
import com.example.demo.dto.response.WorkspaceResponse;
import com.example.demo.model.workspaces.Workspaces;
import com.example.demo.repository.MemberRepository;
import com.example.demo.repository.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {

    private final WorkspaceRepository workspaceRepository;
    private final MemberRepository memberRepository;

    @Override
    public AdminDashboardResponse getDashboardStats() {
        log.info("Admin dashboard istatistikleri isteniyor");
        
        Long workspaceCount = getWorkspaceCount();
        Long activeUserCount = getActiveUserCount();
        
        log.info("Dashboard istatistikleri: Workspace: {}, Aktif Kullanıcı: {}", 
                workspaceCount, activeUserCount);
        
        return new AdminDashboardResponse(workspaceCount, activeUserCount);
    }

    @Override
    public Long getWorkspaceCount() {
        log.info("Workspace sayısı isteniyor");
        Long count = workspaceRepository.countAllWorkspaces();
        log.info("Toplam workspace sayısı: {}", count);
        return count;
    }

    @Override
    public Long getActiveUserCount() {
        log.info("Aktif kullanıcı sayısı isteniyor");
        Long count = memberRepository.countActiveUsers();
        log.info("Aktif kullanıcı sayısı: {}", count);
        return count;
    }

    @Override
    public List<WorkspaceResponse> getAllWorkspaces() {
        log.info("Tüm workspace'ler isteniyor");
        List<Workspaces> workspaces = workspaceRepository.findAll();
        
        List<WorkspaceResponse> responses = workspaces.stream()
                .map(this::convertToWorkspaceResponse)
                .collect(Collectors.toList());
        
        log.info("Toplam {} workspace bulundu", responses.size());
        return responses;
    }

    /**
     * Workspaces entity'sini WorkspaceResponse'a dönüştür
     */
    private WorkspaceResponse convertToWorkspaceResponse(Workspaces workspace) {
        WorkspaceResponse response = new WorkspaceResponse();
        response.setWorkspaceId(workspace.getWorkspaceId());
        response.setWorkspaceName(workspace.getWorkspaceName());
        response.setMemberId(workspace.getMemberId());
        // Role bilgisi workspace'de yok, varsayılan olarak 2 (OWNER) atayalım
        //response.setRoleId(2);
        return response;
    }
} 