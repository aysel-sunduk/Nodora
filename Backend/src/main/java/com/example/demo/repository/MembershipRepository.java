package com.example.demo.repository;

import com.example.demo.model.Membership.Membership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, Long> {

    // workspaceId'ye göre üyeleri getirir
    List<Membership> findByWorkspaceId(Integer workspaceId);

    // memberId ve workspaceId'ye göre tek üyeyi bulur (varsa)
    Optional<Membership> findByMemberIdAndWorkspaceId(Integer memberId, Integer workspaceId);

    // boardId'ye göre üyeleri getirir (opsiyonel, board bazlı üyelik için)
    List<Membership> findByBoardId(Integer boardId);

    // YENİ: workspace üyeliği için boardId'nin null olduğu kaydı bulur.
    Optional<Membership> findByMemberIdAndWorkspaceIdAndBoardIdIsNull(Integer memberId, Integer workspaceId);

    // YENİ: board üyeliği için boardId'nin dolu olduğu kaydı bulur.
    Optional<Membership> findByMemberIdAndBoardId(Integer memberId, Integer boardId);

    // EKLENEN METOT: Global üyeliği için hem boardId'nin hem de workspaceId'nin null olduğu kaydı bulur.
    // Bu metot, CustomPermissionEvaluator'da 'create_workspace' yetkisini kontrol etmek için kullanılır.
    Optional<Membership> findByMemberIdAndWorkspaceIdIsNullAndBoardIdIsNull(Integer memberId);
}