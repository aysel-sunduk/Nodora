package com.example.demo.dto.response;

public class LoginResponse {
    private String token;
    private Integer memberId;
    private Integer roleId; // Yeni eklendi
    private String roleName;

    public LoginResponse() {
    }

    public LoginResponse(String token) {
        this.token = token;
    }

    public LoginResponse(String token, Integer memberId, Integer roleId, String roleName) {
        this.token = token;
        this.memberId = memberId;
        this.roleId = roleId;
        this.roleName = roleName;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }
}