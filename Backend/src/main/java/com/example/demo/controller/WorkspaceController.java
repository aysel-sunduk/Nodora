package com.example.demo.controller;

import com.example.demo.dto.request.WorkspaceRequest;
import com.example.demo.dto.response.WorkspaceResponse;
import com.example.demo.service.WorkspaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workspaces")
@RequiredArgsConstructor
public class WorkspaceController {

    private final WorkspaceService workspaceService;

    // Yeni bir workspace oluşturma
    // Bu işlem için kullanıcının global scope'ta 'MEMBER' rolüne sahip olması gerekir.
    @PostMapping
    @PreAuthorize("hasPermission(null, 'create_workspace')")
    public ResponseEntity<WorkspaceResponse> createWorkspace(@Valid @RequestBody WorkspaceRequest request) {
        WorkspaceResponse createdWorkspace = workspaceService.createWorkspace(request);
        return ResponseEntity.ok(createdWorkspace);
    }

    // Oturum açmış kullanıcının kendi workspacelerini getirir
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<WorkspaceResponse>> getMyWorkspaces() {
        List<WorkspaceResponse> workspaces = workspaceService.getWorkspacesForCurrentUser();
        return ResponseEntity.ok(workspaces);
    }

    // <<< EKSİK OLAN ENDPOINT EKLENDİ >>>
    // Belirli bir memberId'ye ait workspaceleri getirir.
    // Yetkilendirme: Sadece kendi workspace'lerini listelemesine izin verilir.
    @GetMapping("/member/{memberId}")
    @PreAuthorize("hasPermission(#memberId, 'read_workspaces_by_member')")
    public ResponseEntity<List<WorkspaceResponse>> getWorkspacesByMemberId(@PathVariable Integer memberId) {
        List<WorkspaceResponse> workspaces = workspaceService.getWorkspacesByMemberId(memberId);
        return ResponseEntity.ok(workspaces);
    }
    @DeleteMapping("/{workspaceId}")
    @PreAuthorize("hasPermission(#workspaceId, 'delete_workspace')")
    public ResponseEntity<Void> deleteWorkspace(@PathVariable Integer workspaceId) {
        workspaceService.deleteWorkspace(workspaceId);
        return ResponseEntity.noContent().build();
    }
}