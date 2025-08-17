package com.example.demo.dto.request;

public class BoardMemberRequest {
    private Integer memberId;
    private Integer workspaceMemberId;
    private Integer boardId;
    private Integer roleId;

    public Integer getMemberId() { return memberId; }
    public void setMemberId(Integer memberId) { this.memberId = memberId; }
    public Integer getWorkspaceMemberId() { return workspaceMemberId; }
    public void setWorkspaceMemberId(Integer workspaceMemberId) { this.workspaceMemberId = workspaceMemberId; }
    public Integer getBoardId() { return boardId; }
    public void setBoardId(Integer boardId) { this.boardId = boardId; }
    public Integer getRoleId() { return roleId; }
    public void setRoleId(Integer roleId) { this.roleId = roleId; }
} 