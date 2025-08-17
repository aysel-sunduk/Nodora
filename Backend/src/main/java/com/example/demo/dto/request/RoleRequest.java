package com.example.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RoleRequest {
    @NotBlank(message = "Rol adı boş olamaz")
    @Size(max = 10, message = "Rol adı en fazla 10 karakter olabilir")
    private String roleName;

    @NotBlank(message = "Scope boş olamaz")
    @Size(max = 10, message = "Scope en fazla 10 karakter olabilir")
    private String scope;

    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }
    public String getScope() { return scope; }
    public void setScope(String scope) { this.scope = scope; }
}
