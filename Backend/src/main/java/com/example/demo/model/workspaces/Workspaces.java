package com.example.demo.model.workspaces;

import jakarta.persistence.*;

@Entity
@Table(name = "workspaces")
public class Workspaces {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "workspace_id")
    private Integer workspaceId;

    @Column(name = "member_id", nullable = false)
    private Integer memberId;

    @Column(name = "workspace_name", length = 20, nullable = false)
    private String workspaceName;


    // Boş constructor (zorunlu)
    public Workspaces() {}

    // Parametreli constructor (isteğe bağlı)
    public Workspaces(Integer memberId, String workspaceName) {
        this.memberId = memberId;
        this.workspaceName = workspaceName;
    }

    // Getter ve Setter'lar

    public Integer getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(Integer workspaceId) {
        this.workspaceId = workspaceId;
    }

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    public String getWorkspaceName() {
        return workspaceName;
    }

    public void setWorkspaceName(String workspaceName) {
        this.workspaceName = workspaceName;
    }

    // toString (isteğe bağlı)
    @Override
    public String toString() {
        return "Workspaces{" +
                "workspaceId=" + workspaceId +
                ", memberId=" + memberId +
                ", workspaceName='" + workspaceName + '\'' +
                '}';
    }
}