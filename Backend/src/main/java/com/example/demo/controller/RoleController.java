package com.example.demo.controller;

import com.example.demo.dto.request.RoleRequest;
import com.example.demo.dto.response.RoleResponse;
import com.example.demo.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    public ResponseEntity<RoleResponse> createRole(@Valid @RequestBody RoleRequest request) {
        return ResponseEntity.ok(roleService.createRole(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleResponse> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(roleService.getRoleById(id));
    }

    @GetMapping
    public ResponseEntity<List<RoleResponse>> getAll() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoleResponse> update(@PathVariable Integer id, @Valid @RequestBody RoleRequest request) {
        return ResponseEntity.ok(roleService.updateRole(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }
}
