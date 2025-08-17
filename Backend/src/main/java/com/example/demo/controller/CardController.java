package com.example.demo.controller;

import com.example.demo.dto.request.CardRequest;
import com.example.demo.dto.response.CardResponse;
import com.example.demo.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    // Yeni bir kart oluşturur
    @PostMapping
    @PreAuthorize("hasPermission(#request.getListId(), 'create_card')")
    public ResponseEntity<CardResponse> createCard(@RequestBody CardRequest request) {
        CardResponse createdCard = cardService.createCard(request);
        return ResponseEntity.ok(createdCard);
    }

    // ID'ye göre kart getirir
    @GetMapping("/{id}")
    @PreAuthorize("hasPermission(#id, 'read_card')")
    public ResponseEntity<CardResponse> getCardById(@PathVariable Integer id) {
        CardResponse cardResponse = cardService.getCardResponseById(id);
        if (cardResponse != null) {
            return ResponseEntity.ok(cardResponse);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Bir listeye ait tüm kartları getirir.
    @GetMapping("/list/{listId}")
    @PreAuthorize("hasPermission(#listId, 'read_list')")
    public ResponseEntity<List<CardResponse>> getCardsByListId(@PathVariable Integer listId) {
        List<CardResponse> responses = cardService.getCardsByListId(listId);
        return ResponseEntity.ok(responses);
    }

    // Kartı günceller
    @PutMapping("/{id}")
    @PreAuthorize("hasPermission(#id, 'update_card')")
    public ResponseEntity<CardResponse> updateCard(@PathVariable Integer id, @RequestBody CardRequest request) {
        CardResponse updatedCard = cardService.updateCard(id, request);
        if (updatedCard != null) {
            return ResponseEntity.ok(updatedCard);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Kartı siler
    // Service katmanı zaten hata fırlattığı için, Controller'da ek kontrol yapmaya gerek yoktur.
    @DeleteMapping("/{id}")
    @PreAuthorize("hasPermission(#id, 'delete_card')")
    public ResponseEntity<Void> deleteCard(@PathVariable Integer id) {
        cardService.deleteCard(id); // ❗ Metot artık boolean döndürmediği için doğrudan çağrıldı.
        return ResponseEntity.noContent().build();
    }
}