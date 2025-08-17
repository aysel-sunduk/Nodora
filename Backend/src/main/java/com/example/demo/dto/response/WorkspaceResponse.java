// Güncellenmiş WorkspaceResponse.java
package com.example.demo.dto.response;

import lombok.Data;

@Data
public class WorkspaceResponse {
    private Integer workspaceId;
    private String workspaceName;
    private Integer memberId;
    private String roleName;
    private Integer roleId; // <<< YENİ EKLENEN ALAN >>>
}