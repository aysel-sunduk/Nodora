package com.example.demo.controller;

import com.example.demo.dto.response.AdminDashboardResponse;
import com.example.demo.dto.response.WorkspaceResponse;
import com.example.demo.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    /**
     * Admin dashboard istatistiklerini getir
     */
    @GetMapping("/dashboard")
    public ResponseEntity<AdminDashboardResponse> getDashboardStats() {
        AdminDashboardResponse response = adminService.getDashboardStats();
        return ResponseEntity.ok(response);
    }

    /**
     * Workspace sayısını getir
     */
    @GetMapping("/workspaces/count")
    public ResponseEntity<Long> getWorkspaceCount() {
        Long count = adminService.getWorkspaceCount();
        return ResponseEntity.ok(count);
    }

    /**
     * Aktif kullanıcı sayısını getir
     */
    @GetMapping("/users/active/count")
    public ResponseEntity<Long> getActiveUserCount() {
        Long count = adminService.getActiveUserCount();
        return ResponseEntity.ok(count);
    }

    /**
     * Tüm workspace'leri getir (admin için)
     */
    @GetMapping("/workspaces")
    public ResponseEntity<List<WorkspaceResponse>> getAllWorkspaces() {
        List<WorkspaceResponse> workspaces = adminService.getAllWorkspaces();
        return ResponseEntity.ok(workspaces);
    }
} 