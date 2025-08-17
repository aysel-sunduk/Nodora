package com.example.demo.controller;

import com.example.demo.dto.request.BoardRequest;
import com.example.demo.dto.response.BoardResponse;
import com.example.demo.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    // Yeni bir board oluşturma.
    // Sadece workspace sahibi (OWNER) ve lider (LEAD) board oluşturabilir.
    @PostMapping
    @PreAuthorize("hasPermission(#request.getWorkspaceId(), 'create_board')")
    public ResponseEntity<BoardResponse> createBoard(@RequestBody BoardRequest request) {
        BoardResponse createdBoard = boardService.createBoard(request);
        return ResponseEntity.ok(createdBoard);
    }

    // Board'u güncelleme.
    // Board lideri (LEAD) veya sahibi (Workspace OWNER) güncelleyebilir.
    @PutMapping("/{boardId}")
    @PreAuthorize("hasPermission(#boardId, 'update_board')")
    public ResponseEntity<BoardResponse> updateBoard(@PathVariable Integer boardId, @RequestBody BoardRequest request) {
        BoardResponse updatedBoard = boardService.updateBoard(boardId, request);
        return ResponseEntity.ok(updatedBoard);
    }

    // Board'u silme.
    // Board lideri (LEAD) veya sahibi (Workspace OWNER) silebilir.
    @DeleteMapping("/{boardId}")
    @PreAuthorize("hasPermission(#boardId, 'delete_board')")
    public ResponseEntity<Void> deleteBoard(@PathVariable Integer boardId) {
        boardService.deleteBoard(boardId);
        return ResponseEntity.noContent().build();
    }

    // Board'da bir üyeyi takım lideri (LEAD) olarak atama.
    // Sadece board lideri (LEAD) veya workspace sahibi (OWNER) bu işlemi yapabilir.
    @PostMapping("/{boardId}/promote-leader")
    @PreAuthorize("hasPermission(#boardId, 'promote_leader')")
    public ResponseEntity<Void> promoteLeader(@PathVariable Integer boardId, @RequestParam Integer memberId) {
        boardService.promoteLeader(boardId, memberId);
        return ResponseEntity.ok().build();
    }

    // Bir workspace'e ait tüm board'ları listeleme.
    // Sadece workspace üyesi olanlar listeyi görebilir.
    @GetMapping("/workspace/{workspaceId}")
    @PreAuthorize("hasPermission(#workspaceId, 'read_boards_in_workspace')")
    public ResponseEntity<List<BoardResponse>> getBoardsByWorkspaceId(@PathVariable Integer workspaceId) {
        List<BoardResponse> boards = boardService.getBoardsByWorkspaceId(workspaceId);
        return ResponseEntity.ok(boards);
    }
}
