package com.example.demo.service;

import com.example.demo.model.board_members.BoardMember;
import com.example.demo.model.boards.Boards;
import com.example.demo.model.members.Member;
import com.example.demo.model.roles.Roles;
import com.example.demo.model.workspace_members.WorkspaceMember;
import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BoardMemberServiceImpl implements BoardMemberService {

    @Autowired
    private BoardMemberRepository boardMemberRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private WorkspaceMemberRepository workspaceMemberRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private RoleRepository roleRepository;

    // <<< GÜNCELLENMİŞ METOT >>>
    @Override
    @Transactional
    public void addMemberToBoard(Integer boardId, Integer memberId) {
        // Kontrol yetkilendirme katmanında yapıldığı için burada sadece iş mantığını uyguluyoruz.

        // 1. Board, Member ve Role nesnelerini bul
        Boards board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("Board bulunamadı!"));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Üye bulunamadı!"));

        // 2. Varsayılan BOARD MEMBER rolünü bul ve ata
        Roles defaultMemberRole = roleRepository.findByRoleNameAndScope("MEMBER", "BOARD")
                .orElseThrow(() -> new RuntimeException("Varsayılan BOARD MEMBER rolü bulunamadı!"));

        // 3. Üyenin zaten board'da olup olmadığını kontrol et
        if (boardMemberRepository.findByBoard_BoardIdAndMember_MemberId(boardId, memberId).isPresent()) {
            throw new RuntimeException("Bu üye zaten board'da mevcut.");
        }

        // 4. Üyenin aynı zamanda workspace üyesi olduğunu doğrula
        WorkspaceMember workspaceMember = workspaceMemberRepository
                .findByWorkspace_WorkspaceIdAndMember_MemberId(board.getWorkspaceId(), memberId)
                .orElseThrow(() -> new RuntimeException("Üye, board'un ait olduğu workspace'de bulunamıyor!"));

        // 5. BoardMember nesnesini oluştur ve kaydet
        BoardMember newBoardMember = new BoardMember();
        newBoardMember.setBoard(board);
        newBoardMember.setMember(member);
        newBoardMember.setRoleId(defaultMemberRole.getRoleId()); // Varsayılan rolü ata
        newBoardMember.setWorkspaceMember(workspaceMember);
        boardMemberRepository.save(newBoardMember);

        System.out.println("LOG: memberId=" + memberId + ", action=ADD_MEMBER_TO_BOARD, boardId=" + boardId);
    }

    @Override
    @Transactional
    public void removeMemberFromBoard(Integer boardId, Integer memberId) {
        boardMemberRepository.deleteByBoard_BoardIdAndMember_MemberId(boardId, memberId);
    }

    @Override
    public List<BoardMember> getBoardMembers(Integer boardId) {
        return boardMemberRepository.findAllByBoardId(boardId);
    }
}