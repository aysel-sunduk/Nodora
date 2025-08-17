package com.example.demo.service;

import com.example.demo.dto.request.RoleRequest;
import com.example.demo.dto.response.RoleResponse;

import java.util.List;

public interface RoleService {
    RoleResponse createRole(RoleRequest request);
    RoleResponse getRoleById(Integer id);
    List<RoleResponse> getAllRoles();
    RoleResponse updateRole(Integer id, RoleRequest request);
    void deleteRole(Integer id);
}
