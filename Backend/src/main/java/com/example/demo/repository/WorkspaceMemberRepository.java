package com.example.demo.repository;

import com.example.demo.model.members.Member;
import com.example.demo.model.workspace_members.WorkspaceMember;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import java.util.List;

public interface WorkspaceMemberRepository extends JpaRepository<WorkspaceMember, Integer> {

    Optional<WorkspaceMember> findByWorkspace_WorkspaceIdAndMember_MemberId(Integer workspaceId, Integer memberId);

    // Sorunu çözen düzeltme burada yapıldı.
    // Bu anotasyon, ilgili 'member' ve 'role' nesnelerini tek bir sorguda yükler.
    @EntityGraph(attributePaths = {"member", "role"})
    List<WorkspaceMember> findByWorkspace_WorkspaceId(Integer workspaceId);

    List<WorkspaceMember> findByMember_MemberId(Integer memberId);
    void deleteByWorkspace_WorkspaceId(Integer workspaceId);

    @Query("SELECT wm FROM WorkspaceMember wm WHERE wm.member = :member AND wm.workspace IS NULL")
    Optional<WorkspaceMember> findByMemberAndWorkspaceIdIsNull(@Param("member") Member member);

    @Query("SELECT wm FROM WorkspaceMember wm WHERE wm.member.memberId = :memberId AND wm.workspace.workspaceId IS NULL")
    Optional<WorkspaceMember> findGlobalMemberByMemberId(@Param("memberId") Integer memberId);
}
