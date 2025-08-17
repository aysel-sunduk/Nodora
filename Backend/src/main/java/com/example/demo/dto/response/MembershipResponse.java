package com.example.demo.dto.response;

public class MembershipResponse {
    private Integer id;
    private Integer memberId;
    private Integer boardId;
    private Integer workspaceId;
    private Integer roleId;

    // Getter & Setter
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getMemberId() { return memberId; }
    public void setMemberId(Integer memberId) { this.memberId = memberId; }

    public Integer getBoardId() { return boardId; }
    public void setBoardId(Integer boardId) { this.boardId = boardId; }

    public Integer getWorkspaceId() { return workspaceId; }
    public void setWorkspaceId(Integer workspaceId) { this.workspaceId = workspaceId; }

    public Integer getRoleId() { return roleId; }
    public void setRoleId(Integer roleId) { this.roleId = roleId; }
}
