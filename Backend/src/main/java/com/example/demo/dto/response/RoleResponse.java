package com.example.demo.dto.response;

import lombok.Data;

@Data
public class RoleResponse {
    private Integer roleId;
    private String roleName;
    private String scope;

    public Integer getRoleId() { return roleId; }
    public void setRoleId(Integer roleId) { this.roleId = roleId; }
    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }
    public String getScope() { return scope; }
    public void setScope(String scope) { this.scope = scope; }
}
