package com.example.demo.controller;

import com.example.demo.dto.request.CardLabelsRequest;
import com.example.demo.dto.response.CardLabelsResponse;
import com.example.demo.service.CardLabelsService;
import com.example.demo.validation.CardLabelsValidation;
import com.example.demo.exception.CardLabelNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // ❗ Yeni import
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cardlabels")
public class CardLabelsController {

    @Autowired
    private CardLabelsService cardLabelsService;

    @Autowired
    private CardLabelsValidation cardLabelsValidation;

    // Tüm CardLabel kayıtlarını getir
    // Bu endpoint'in mantığı şüphelidir. Muhtemelen bir karta bağlı etiketleri getirir.
    // Şimdilik yetkisiz erişime kapatıyoruz.
    @GetMapping
    @PreAuthorize("hasAuthority('admin')") // Sadece adminler için
    public ResponseEntity<List<CardLabelsResponse>> getAll() {
        return ResponseEntity.ok(cardLabelsService.getAllCardLabels());
    }

    // ID ile CardLabel getir
    // Bu işlem için card'a 'read' yetkisi olması yeterlidir.
    @GetMapping("/{id}")
    @PreAuthorize("hasPermission(#id, 'read_card_label')")
    public ResponseEntity<CardLabelsResponse> getById(@PathVariable Integer id) {
        var result = cardLabelsService.getCardLabelById(id);
        if (result == null) throw new CardLabelNotFoundException(id);
        return ResponseEntity.ok(result);
    }

    // Yeni CardLabel ekle
    // Eklemek için card'a 'create_card_label' yetkisi olması gerekir.
    // Yetki kontrolü için dto'dan cardId'yi alıyoruz.
    @PostMapping
    @PreAuthorize("hasPermission(#dto.getCardId(), 'create_card_label')")
    public ResponseEntity<CardLabelsResponse> create(@RequestBody CardLabelsRequest dto) {
        cardLabelsValidation.validate(dto);
        return ResponseEntity.ok(cardLabelsService.createCardLabel(dto));
    }

    // CardLabel güncelle
    // Güncellemek için card'a 'update_card_label' yetkisi olması gerekir.
    // Yetki kontrolü için id'yi kullanıyoruz.
    @PutMapping("/{id}")
    @PreAuthorize("hasPermission(#id, 'update_card_label')")
    public ResponseEntity<CardLabelsResponse> update(@PathVariable Integer id, @RequestBody CardLabelsRequest dto) {
        cardLabelsValidation.validate(dto);
        var updated = cardLabelsService.updateCardLabel(id, dto);
        if (updated == null) throw new CardLabelNotFoundException(id);
        return ResponseEntity.ok(updated);
    }

    // CardLabel sil
    // Silmek için card'a 'delete_card_label' yetkisi olması gerekir.
    @DeleteMapping("/{id}")
    @PreAuthorize("hasPermission(#id, 'delete_card_label')")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        boolean deleted = cardLabelsService.deleteCardLabel(id);
        if (!deleted) throw new CardLabelNotFoundException(id);
        return ResponseEntity.noContent().build();
    }
}