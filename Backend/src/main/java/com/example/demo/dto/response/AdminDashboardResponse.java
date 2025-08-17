package com.example.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardResponse {
    
    private Long workspaceCount;
    private Long activeUserCount;
    private String message;
    
    public AdminDashboardResponse(Long workspaceCount, Long activeUserCount) {
        this.workspaceCount = workspaceCount;
        this.activeUserCount = activeUserCount;
        this.message = "Admin dashboard istatistikleri başarıyla getirildi";
    }
} 