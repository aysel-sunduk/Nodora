package com.example.demo.repository;

import com.example.demo.model.boards.Boards;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<Boards, Integer> {

    List<Boards> findByWorkspaceId(Integer workspaceId);

    List<Boards> findByTitleContainingIgnoreCase(String title);

    @Query("SELECT DISTINCT b FROM Boards b JOIN BoardMember bm ON b.boardId = bm.board.boardId WHERE bm.member.memberId = :memberId")
    List<Boards> findAccessibleBoards(@Param("memberId") Integer memberId);

    @Query("SELECT COUNT(bm) > 0 FROM BoardMember bm WHERE bm.board.boardId = :boardId AND bm.member.memberId = :memberId AND bm.roleId = 3")
    boolean isBoardLeader(@Param("boardId") Integer boardId, @Param("memberId") Integer memberId);

    @Query("SELECT COUNT(bm) FROM BoardMember bm WHERE bm.board.boardId = :boardId AND bm.roleId = :roleId")
    int countMembersWithRole(@Param("boardId") Integer boardId, @Param("roleId") Integer roleId);

    @Query("SELECT bm.roleId FROM BoardMember bm WHERE bm.board.boardId = :boardId AND bm.member.memberId = :memberId")
    Optional<Integer> findRoleByBoardAndMember(@Param("boardId") Integer boardId, @Param("memberId") Integer memberId);

    // Yeni eklenen metod
    Optional<Boards> findByBoardIdAndMemberId(Integer boardId, Integer memberId);
} 