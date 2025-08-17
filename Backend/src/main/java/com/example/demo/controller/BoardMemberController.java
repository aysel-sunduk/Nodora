package com.example.demo.controller;

import com.example.demo.model.board_members.BoardMember;
import com.example.demo.service.BoardMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/board-members")
public class BoardMemberController {

    @Autowired
    private BoardMemberService boardMemberService;

    // Board'a üye ekleme
    // Yetkilendirme kontrolü için boardId kullanılır.
    // Varsayılan rol (MEMBER) otomatik olarak atanır.
    @PostMapping("/add")
    @PreAuthorize("hasPermission(#boardId, 'add_member_to_board')")
    public ResponseEntity<?> addMemberToBoard(@RequestParam Integer boardId, @RequestParam Integer memberId) {
        boardMemberService.addMemberToBoard(boardId, memberId);
        return ResponseEntity.ok("Üye board'a başarıyla eklendi.");
    }

    // Board'dan üye çıkarma
    @DeleteMapping("/remove")
    @PreAuthorize("hasPermission(#boardId, 'remove_member_from_board')")
    public ResponseEntity<?> removeMemberFromBoard(@RequestParam Integer boardId, @RequestParam Integer memberId) {
        boardMemberService.removeMemberFromBoard(boardId, memberId);
        return ResponseEntity.ok("Üye board'dan çıkarıldı.");
    }

    // Board üyelerini listeleme
    @GetMapping("/list")
    @PreAuthorize("hasPermission(#boardId, 'read_board_members')")
    public ResponseEntity<List<BoardMember>> getBoardMembers(@RequestParam Integer boardId) {
        return ResponseEntity.ok(boardMemberService.getBoardMembers(boardId));
    }
}