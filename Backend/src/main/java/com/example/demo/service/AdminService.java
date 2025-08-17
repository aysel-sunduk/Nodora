package com.example.demo.service;

import com.example.demo.dto.response.AdminDashboardResponse;
import com.example.demo.dto.response.WorkspaceResponse;

import java.util.List;

public interface AdminService {
    
    /**
     * Admin dashboard istatistiklerini getir
     */
    AdminDashboardResponse getDashboardStats();
    
    /**
     * Workspace sayısını getir
     */
    Long getWorkspaceCount();
    
    /**
     * Aktif kullanıcı sayısını getir
     */
    Long getActiveUserCount();
    
    /**
     * Tüm workspace'leri getir (admin için)
     */
    List<WorkspaceResponse> getAllWorkspaces();
} 