package com.example.demo.controller;

import com.example.demo.dto.request.MembershipRequest;
import com.example.demo.dto.response.MembershipResponse;
import com.example.demo.service.MembershipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/membership")
public class MembershipController {

    @Autowired
    private MembershipService MembershipService;

    @PostMapping
    public ResponseEntity<MembershipResponse> createMembership(@RequestBody MembershipRequest request) {
        MembershipResponse response = MembershipService.createMembership(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/workspace/{workspaceId}")
    public ResponseEntity<List<MembershipResponse>> getByWorkspace(@PathVariable Integer workspaceId) {
        List<MembershipResponse> list = MembershipService.getAllByWorkspaceId(workspaceId);
        return ResponseEntity.ok(list);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMembership(@PathVariable Long id) {
        MembershipService.deleteMembership(id);
        return ResponseEntity.noContent().build();
    }
}
