package com.example.demo.service;

import com.example.demo.dto.request.RoleRequest;
import com.example.demo.dto.response.RoleResponse;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.roles.Roles;
import com.example.demo.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository repository;

    @Override
    public RoleResponse createRole(RoleRequest request) {
        Roles role = new Roles(request.getRoleName(), request.getScope());
        Roles saved = repository.save(role);
        return convertToResponse(saved);
    }

    @Override
    public RoleResponse getRoleById(Integer id) {
        Roles role = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol bulunamadı"));
        return convertToResponse(role);
    }

    @Override
    public List<RoleResponse> getAllRoles() {
        return repository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public RoleResponse updateRole(Integer id, RoleRequest request) {
        Roles role = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol bulunamadı"));

        role.setRoleName(request.getRoleName());
        role.setScope(request.getScope());

        return convertToResponse(repository.save(role));
    }

    @Override
    public void deleteRole(Integer id) {
        Roles role = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol bulunamadı"));
        repository.delete(role);
    }

    private RoleResponse convertToResponse(Roles role) {
        RoleResponse response = new RoleResponse();
        response.setRoleId(role.getRoleId());
        response.setRoleName(role.getRoleName());
        response.setScope(role.getScope());
        return response;
    }
}
