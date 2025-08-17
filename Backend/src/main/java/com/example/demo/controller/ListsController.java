package com.example.demo.controller;

import com.example.demo.dto.request.ListsRequest;
import com.example.demo.dto.response.ListsResponse;
import com.example.demo.service.ListsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lists")
@RequiredArgsConstructor
public class ListsController {

    private final ListsService listsService;

    // Yeni bir liste oluşturma.
    // Kullanıcının, listeyi oluşturacağı board'da üye veya lider olması gerekir.
    @PostMapping
    @PreAuthorize("hasPermission(#request.getBoardId(), 'create_list')")
    public ResponseEntity<ListsResponse> createList(@RequestBody ListsRequest request) {
        ListsResponse response = listsService.createList(request);
        return ResponseEntity.ok(response);
    }

    // Board'a ait tüm listeleri getirme.
    @GetMapping("/board/{boardId}")
    @PreAuthorize("hasPermission(#boardId, 'read_board')")
    public ResponseEntity<List<ListsResponse>> getListsByBoardId(@PathVariable Integer boardId) {
        List<ListsResponse> responses = listsService.getListsByBoardId(boardId);
        return ResponseEntity.ok(responses);
    }

    // ID'ye göre liste getir
    @GetMapping("/{id}")
    @PreAuthorize("hasPermission(#id, 'read_list')")
    public ResponseEntity<ListsResponse> getListById(@PathVariable Integer id) {
        ListsResponse response = listsService.getListById(id);
        if (response != null) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.notFound().build();
    }

    // Liste güncelleme
    @PutMapping("/{id}")
    @PreAuthorize("hasPermission(#id, 'update_list')")
    public ResponseEntity<ListsResponse> updateList(@PathVariable Integer id, @RequestBody ListsRequest request) {
        ListsResponse response = listsService.updateList(id, request);
        if (response != null) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.notFound().build();
    }

    // Liste silme
    @DeleteMapping("/{id}")
    @PreAuthorize("hasPermission(#id, 'delete_list')")
    public ResponseEntity<Void> deleteList(@PathVariable Integer id) {
        boolean deleted = listsService.deleteList(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
