package com.example.demo.service;

import com.example.demo.dto.request.MembershipRequest;
import com.example.demo.dto.response.MembershipResponse;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Membership.Membership;
import com.example.demo.repository.MembershipRepository;
import com.example.demo.service.MembershipService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MembershipServiceImpl implements MembershipService {

    private final MembershipRepository membershipRepository;

    @Override
    public MembershipResponse createMembership(MembershipRequest request) {
        Membership membership = new Membership();
        membership.setMemberId(request.getMemberId());
        membership.setBoardId(request.getBoardId());
        membership.setWorkspaceId(request.getWorkspaceId());
        membership.setRoleId(request.getRoleId());

        Membership saved = membershipRepository.save(membership);
        return convertToResponse(saved);
    }

    @Override
    public List<MembershipResponse> getAllByWorkspaceId(Integer workspaceId) {
        List<Membership> memberships = membershipRepository.findByWorkspaceId(workspaceId);
        return memberships.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteMembership(Long id) {
        Membership membership = membershipRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Membership bulunamadı: " + id));
        membershipRepository.delete(membership);
    }

    private MembershipResponse convertToResponse(Membership membership) {
        MembershipResponse response = new MembershipResponse();
        response.setId(membership.getId());
        response.setMemberId(membership.getMemberId());
        response.setBoardId(membership.getBoardId());
        response.setWorkspaceId(membership.getWorkspaceId());
        response.setRoleId(membership.getRoleId());
        return response;
    }
}