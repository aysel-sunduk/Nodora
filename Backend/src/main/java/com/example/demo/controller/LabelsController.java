package com.example.demo.controller;

import com.example.demo.dto.request.LabelsRequest;
import com.example.demo.dto.response.LabelsResponse;
import com.example.demo.service.LabelsService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity; // ❗ ResponseEntity kullanmak daha iyi bir pratik
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/labels")
public class LabelsController {

    private final LabelsService labelsService;

    public LabelsController(LabelsService labelsService) {
        this.labelsService = labelsService;
    }

    /**
     * Tüm etiketleri getirir.
     * Bu endpoint'in bir board'a bağlı olması gerekir. Aksi halde tüm sistemdeki etiketleri listeler.
     * Güvenlik için, bu endpoint'i bir `boardId` parametresi alacak şekilde düzenlemek en doğrusudur.
     * Şu anki haliyle, yetkilendirme eklemek zordur.
     */
    @GetMapping
    public ResponseEntity<List<LabelsResponse>> getAllLabels() {
        // Bu endpoint'i, tüm etiketleri değil, bir board'a ait etiketleri listeleyecek şekilde düşünmeliyiz.
        // Örneğin: @GetMapping("/boards/{boardId}")
        // Ancak mevcut yapınızda bu yok, bu yüzden yetkilendirme eklemiyorum.
        return ResponseEntity.ok(labelsService.getAllLabels());
    }

    /**
     * ID'ye göre bir etiketi getirir.
     * Yetkilendirme kontrolü için labelId kullanılır.
     * 'read_label' yetkisi, kullanıcının etiketin ait olduğu board'da üye olmasını gerektirir.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasPermission(#id, 'read_label')")
    public ResponseEntity<LabelsResponse> getLabel(@PathVariable Integer id) {
        return ResponseEntity.ok(labelsService.getLabelById(id));
    }

    /**
     * Yeni bir etiket oluşturur.
     * Yetkilendirme kontrolü için request'deki boardId kullanılır.
     * 'create_label' yetkisi, kullanıcının board'da yetkili bir role sahip olmasını gerektirir (OWNER, LEADER gibi).
     */
    @PostMapping
    @PreAuthorize("hasPermission(#request.getBoardId(), 'create_label')")
    public ResponseEntity<LabelsResponse> createLabel(@Valid @RequestBody LabelsRequest request) {
        return ResponseEntity.ok(labelsService.createLabel(request));
    }

    /**
     * Bir etiketi siler.
     * Yetkilendirme kontrolü için labelId kullanılır.
     * 'delete_label' yetkisi, kullanıcının board'da OWNER veya ADMIN olmasını gerektirir.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasPermission(#id, 'delete_label')")
    public ResponseEntity<Void> deleteLabel(@PathVariable Integer id) {
        labelsService.deleteLabel(id);
        return ResponseEntity.noContent().build();
    }
}