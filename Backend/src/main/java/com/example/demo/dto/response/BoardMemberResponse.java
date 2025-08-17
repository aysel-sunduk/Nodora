package com.example.demo.dto.response;

public class BoardMemberResponse {
    private Integer boardMemberId;
    private Integer memberId;
    private Integer workspaceMemberId;
    private Integer boardId;
    //private Integer roleId;

    public Integer getBoardMemberId() { return boardMemberId; }
    public void setBoardMemberId(Integer boardMemberId) { this.boardMemberId = boardMemberId; }
    public Integer getMemberId() { return memberId; }
    public void setMemberId(Integer memberId) { this.memberId = memberId; }
    public Integer getWorkspaceMemberId() { return workspaceMemberId; }
    public void setWorkspaceMemberId(Integer workspaceMemberId) { this.workspaceMemberId = workspaceMemberId; }
    public Integer getBoardId() { return boardId; }
    public void setBoardId(Integer boardId) { this.boardId = boardId; }
    //public Integer getRoleId() { return roleId; }
    //public void setRoleId(Integer roleId) { this.roleId = roleId; }
} 