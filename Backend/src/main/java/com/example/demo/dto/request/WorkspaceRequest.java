package com.example.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class WorkspaceRequest {

    @NotNull(message = "Üye ID boş olamaz")
    private Integer memberId;

    @NotBlank(message = "Workspace adı boş olamaz")
    @Size(max = 20, message = "Workspace adı en fazla 20 karakter olabilir")
    private String workspaceName;

    public Integer getMemberId() { return memberId; }
    public void setMemberId(Integer memberId) { this.memberId = memberId; }
    public String getWorkspaceName() { return workspaceName; }
    public void setWorkspaceName(String workspaceName) { this.workspaceName = workspaceName; }
}