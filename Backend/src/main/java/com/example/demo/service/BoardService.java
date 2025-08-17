package com.example.demo.service;

import com.example.demo.dto.request.BoardRequest;
import com.example.demo.dto.response.BoardResponse;
import com.example.demo.model.board_members.BoardMember;
import com.example.demo.model.boards.Boards;
import com.example.demo.model.members.Member;
import com.example.demo.model.roles.Roles;
import com.example.demo.model.workspace_members.WorkspaceMember;
import com.example.demo.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardService {

    private final BoardRepository boardRepository;
    private final BoardMemberRepository boardMemberRepository;
    private final RoleRepository roleRepository;
    private final MemberRepository memberRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;

    private static final String OWNER_ROLE = "OWNER";
    private static final String LEAD_ROLE = "LEAD";
    private static final String MEMBER_ROLE = "MEMBER";

    @Transactional
    public BoardResponse createBoard(BoardRequest request) {
        Integer currentMemberId = getCurrentMemberId();
        Member currentMember = memberRepository.findById(currentMemberId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        Boards newBoard = new Boards();
        newBoard.setWorkspaceId(request.getWorkspaceId());
        newBoard.setBgColor(request.getBgColor());
        newBoard.setTitle(request.getTitle());
        newBoard.setMemberId(currentMemberId);
        Boards savedBoard = boardRepository.save(newBoard);
        log.info("Yeni board oluşturuldu: {}", savedBoard.getBoardId());

        // Veritabanınızdaki rollere göre, BOARD'da bir OWNER olmadığı için
        // board'u oluşturan kişiye BOARD LEAD rolünü atıyoruz.
        Roles creatorRole = roleRepository.findByRoleNameAndScope(LEAD_ROLE, "BOARD")
                .orElseThrow(() -> {
                    log.error("LEAD rolü (kapsam: BOARD) veritabanında bulunamadı!");
                    return new RuntimeException("LEAD rolü bulunamadı!");
                });

        WorkspaceMember workspaceMember = workspaceMemberRepository
                .findByWorkspace_WorkspaceIdAndMember_MemberId(savedBoard.getWorkspaceId(), currentMemberId)
                .orElseThrow(() -> new RuntimeException("Workspace üyesi bulunamadı!"));

        BoardMember creator = new BoardMember();
        creator.setBoard(savedBoard);
        creator.setMember(currentMember);
        creator.setRoleId(creatorRole.getRoleId());
        creator.setWorkspaceMember(workspaceMember);
        boardMemberRepository.save(creator);
        log.info("Board oluşturan üye (memberId: {}) otomatik olarak board'a (boardId: {}) LEAD olarak atandı.", currentMemberId, savedBoard.getBoardId());

        return toResponse(savedBoard);
    }

    @Transactional
    public BoardResponse updateBoard(Integer boardId, BoardRequest request) {
        Integer currentMemberId = getCurrentMemberId();
        Boards board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("Board bulunamadı!"));

        board.setTitle(request.getTitle());
        board.setBgColor(request.getBgColor());

        Boards saved = boardRepository.save(board);
        log.info("LOG: memberId={}, action=BOARD_UPDATE, boardId={}", currentMemberId, boardId);

        return toResponse(saved);
    }

    @Transactional
    public void deleteBoard(Integer boardId) {
        Integer deleterMemberId = getCurrentMemberId();
        boardRepository.deleteById(boardId);
        log.info("LOG: memberId={}, action=BOARD_DELETE, boardId={}", deleterMemberId, boardId);
    }

    @Transactional
    public void promoteLeader(Integer boardId, Integer memberId) {
        BoardMember boardMember = boardMemberRepository.findByBoard_BoardIdAndMember_MemberId(boardId, memberId)
                .orElseThrow(() -> new RuntimeException("Üye board'da bulunamadı!"));

        Roles leaderRole = roleRepository.findByRoleNameAndScope(LEAD_ROLE, "BOARD")
                .orElseThrow(() -> new RuntimeException("LEAD rolü bulunamadı!"));

        boardMember.setRoleId(leaderRole.getRoleId());
        boardMemberRepository.save(boardMember);
        log.info("LOG: memberId={}, action=PROMOTE_LEADER, boardId={}", memberId, boardId);
    }

    public List<BoardResponse> getBoardsByWorkspaceId(Integer workspaceId) {
        return boardRepository.findByWorkspaceId(workspaceId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private BoardResponse toResponse(Boards board) {
        BoardResponse response = new BoardResponse();
        response.setBoardId(board.getBoardId());
        response.setWorkspaceId(board.getWorkspaceId());
        response.setBgColor(board.getBgColor());
        response.setTitle(board.getTitle());
        response.setMemberId(board.getMemberId());
        return response;
    }

    private Integer getCurrentMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof UserDetails)) {
            throw new RuntimeException("Kullanıcı oturum açmamış veya kimlik doğrulama bilgisi eksik!");
        }
        String email = ((UserDetails) authentication.getPrincipal()).getUsername();
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı")).getMemberId();
    }
}
