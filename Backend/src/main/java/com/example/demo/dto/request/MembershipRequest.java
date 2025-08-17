package com.example.demo.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data                 // getter, setter, toString, equals, hashCode üretir
@NoArgsConstructor    // parametresiz constructor
@AllArgsConstructor   // tüm alanları parametre alan constructor
public class MembershipRequest {
    private Integer memberId;
    private Integer boardId;
    private Integer workspaceId;
    private Integer roleId;

    public Integer getMemberId() { return memberId; }
    public void setMemberId(Integer memberId) { this.memberId = memberId; }
    public Integer getBoardId() { return boardId; }
    public void setBoardId(Integer boardId) { this.boardId = boardId; }
    public Integer getWorkspaceId() { return workspaceId; }
    public void setWorkspaceId(Integer workspaceId) { this.workspaceId = workspaceId; }
    public Integer getRoleId() { return roleId; }
    public void setRoleId(Integer roleId) { this.roleId = roleId; }
}
