package com.example.demo.service;

import com.example.demo.dto.request.MembershipRequest;
import com.example.demo.dto.response.MembershipResponse;

import java.util.List;

public interface MembershipService {

    MembershipResponse createMembership(MembershipRequest request);

    List<MembershipResponse> getAllByWorkspaceId(Integer workspaceId);

    void deleteMembership(Long id);
}