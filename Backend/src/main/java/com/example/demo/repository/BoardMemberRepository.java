package com.example.demo.repository;

import com.example.demo.model.board_members.BoardMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardMemberRepository extends JpaRepository<BoardMember, Integer> {

    // Temel sorgular
    Optional<BoardMember> findByBoard_BoardIdAndRoleId(Integer boardId, Integer roleId);
    Optional<BoardMember> findByBoard_BoardIdAndMember_MemberId(Integer boardId, Integer memberId);
    boolean existsByBoard_BoardIdAndMember_MemberId(Integer boardId, Integer memberId);
    void deleteByBoard_BoardIdAndMember_MemberId(Integer boardId, Integer memberId);

    // Board üyeleriyle ilgili sorgular
    @Query("SELECT bm FROM BoardMember bm WHERE bm.board.boardId = :boardId ORDER BY bm.roleId")
    List<BoardMember> findAllByBoardId(@Param("boardId") Integer boardId);

    @Query("SELECT COUNT(bm) FROM BoardMember bm WHERE bm.board.boardId = :boardId")
    int countMembersInBoard(@Param("boardId") Integer boardId);

    // Rol ve yetki yönetimi
    @Query("SELECT bm.roleId FROM BoardMember bm WHERE bm.board.boardId = :boardId AND bm.member.memberId = :memberId")
    Optional<Integer> findRoleByBoardAndMember(@Param("boardId") Integer boardId,
                                               @Param("memberId") Integer memberId);

    @Query("SELECT bm FROM BoardMember bm WHERE bm.board.boardId = :boardId AND bm.roleId = 3")
    Optional<BoardMember> findLeaderByBoard(@Param("boardId") Integer boardId);

    @Modifying
    @Query("UPDATE BoardMember bm SET bm.roleId = 5 WHERE bm.board.boardId = :boardId AND bm.roleId = 3")
    void demotePreviousLeader(@Param("boardId") Integer boardId);

    @Modifying
    @Query("UPDATE BoardMember bm SET bm.roleId = 3 WHERE bm.board.boardId = :boardId AND bm.member.memberId = :memberId")
    void promoteToLeader(@Param("boardId") Integer boardId, @Param("memberId") Integer memberId);

    @Query("SELECT COUNT(bm) > 0 FROM BoardMember bm " +
            "WHERE bm.member.memberId = :memberId AND bm.board.boardId = :boardId AND bm.roleId IN :roleIds")
    boolean hasPermission(@Param("memberId") Integer memberId,
                          @Param("boardId") Integer boardId,
                          @Param("roleIds") List<Integer> roleIds);

    // Ek yardımcı metodlar
    @Query("SELECT bm FROM BoardMember bm WHERE bm.member.memberId = :memberId")
    List<BoardMember> findAllByMemberId(@Param("memberId") Integer memberId);

    @Query("SELECT COUNT(bm) > 0 FROM BoardMember bm " +
            "WHERE bm.board.boardId = :boardId AND bm.member.memberId = :memberId AND bm.roleId = 3")
    boolean isUserLeaderOfBoard(@Param("boardId") Integer boardId,
                                @Param("memberId") Integer memberId);

    @Query("SELECT COUNT(bm) FROM BoardMember bm WHERE bm.board.boardId = :boardId AND bm.roleId = :roleId")
    int countMembersWithRole(@Param("boardId") Integer boardId, @Param("roleId") Integer roleId);
}