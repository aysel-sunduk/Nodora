package com.example.demo.controller;

import com.example.demo.dto.request.ChecklistRequestDTO;
import com.example.demo.dto.request.ChecklistItemRequestDTO;
import com.example.demo.dto.response.ChecklistResponseDTO;
import com.example.demo.dto.response.ChecklistDetailResponseDTO;
import com.example.demo.dto.response.ChecklistItemResponseDTO;
import com.example.demo.dto.response.ChecklistProgressDTO;
import com.example.demo.service.ChecklistService;
import com.example.demo.service.ChecklistItemService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // ❗ Yeni import
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Checklist ve ChecklistItem işlemleri için REST API Controller sınıfı
 *
 * Bu controller checklist'ler ve checklist item'ları ile ilgili tüm HTTP isteklerini karşılar.
 * CRUD operasyonları, özel işlemler ve raporlama endpoint'leri içerir.
 *
 * @author Nodora Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/checklists")
@Tag(name = "Checklist Controller", description = "Checklist ve checklist item yönetimi için API endpoint'leri")
public class ChecklistController {

    private final ChecklistService checklistService;
    private final ChecklistItemService checklistItemService;

    /**
     * Constructor injection ile service dependency'lerini alır
     *
     * @param checklistService Checklist işlemleri servisi
     * @param checklistItemService ChecklistItem işlemleri servisi
     */
    @Autowired
    public ChecklistController(ChecklistService checklistService,
                               ChecklistItemService checklistItemService) {
        this.checklistService = checklistService;
        this.checklistItemService = checklistItemService;
    }

    // ================================
    // CHECKLIST ENDPOINT'LERİ
    // ================================

    /**
     * Yeni bir checklist oluşturur
     * Yetkilendirme kontrolü için requestDTO içindeki cardId kullanılır.
     */
    @PostMapping
    @PreAuthorize("hasPermission(#requestDTO.getCardId(), 'create_checklist')")
    @Operation(summary = "Yeni checklist oluştur",
            description = "Belirtilen kart için yeni bir checklist oluşturur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Checklist başarıyla oluşturuldu"),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek verisi"),
            @ApiResponse(responseCode = "404", description = "Kart bulunamadı")
    })
    public ResponseEntity<ChecklistResponseDTO> createChecklist(
            @Valid @RequestBody ChecklistRequestDTO requestDTO) {

        ChecklistResponseDTO response = checklistService.createChecklist(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Belirli bir checklist'i ID ile getirir
     * Yetkilendirme kontrolü için checklistId kullanılır.
     */
    @GetMapping("/{checklistId}")
    @PreAuthorize("hasPermission(#checklistId, 'read_checklist')")
    @Operation(summary = "Checklist getir",
            description = "ID'si verilen checklist'i getirir")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Checklist başarıyla getirildi"),
            @ApiResponse(responseCode = "404", description = "Checklist bulunamadı")
    })
    public ResponseEntity<ChecklistResponseDTO> getChecklistById(
            @Parameter(description = "Checklist ID", required = true)
            @PathVariable Integer checklistId) {

        ChecklistResponseDTO response = checklistService.getChecklistById(checklistId);
        return ResponseEntity.ok(response);
    }

    /**
     * Checklist'i item'larıyla birlikte detaylı olarak getirir
     * Yetkilendirme kontrolü için checklistId kullanılır.
     */
    @GetMapping("/{checklistId}/details")
    @PreAuthorize("hasPermission(#checklistId, 'read_checklist')")
    @Operation(summary = "Checklist detaylarını getir",
            description = "Checklist'i tüm item'larıyla birlikte getirir")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Checklist detayları başarıyla getirildi"),
            @ApiResponse(responseCode = "404", description = "Checklist bulunamadı")
    })
    public ResponseEntity<ChecklistDetailResponseDTO> getChecklistWithDetails(
            @Parameter(description = "Checklist ID", required = true)
            @PathVariable Integer checklistId) {

        ChecklistDetailResponseDTO response = checklistService.getChecklistWithDetails(checklistId);
        return ResponseEntity.ok(response);
    }

    /**
     * Belirli bir karta ait tüm checklist'leri getirir
     * Yetkilendirme kontrolü için cardId kullanılır.
     */
    @GetMapping("/card/{cardId}")
    @PreAuthorize("hasPermission(#cardId, 'read_card')")
    @Operation(summary = "Karta ait checklist'leri getir",
            description = "Belirtilen karta ait tüm checklist'leri getirir")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Checklist'ler başarıyla getirildi"),
            @ApiResponse(responseCode = "404", description = "Kart bulunamadı")
    })
    public ResponseEntity<List<ChecklistResponseDTO>> getChecklistsByCardId(
            @Parameter(description = "Kart ID", required = true)
            @PathVariable Integer cardId) {

        List<ChecklistResponseDTO> response = checklistService.getChecklistsByCardId(cardId);
        return ResponseEntity.ok(response);
    }

    /**
     * Mevcut bir checklist'i günceller
     * Yetkilendirme kontrolü için checklistId kullanılır.
     */
    @PutMapping("/{checklistId}")
    @PreAuthorize("hasPermission(#checklistId, 'update_checklist')")
    @Operation(summary = "Checklist güncelle",
            description = "Mevcut checklist'i günceller")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Checklist başarıyla güncellendi"),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek verisi"),
            @ApiResponse(responseCode = "404", description = "Checklist bulunamadı")
    })
    public ResponseEntity<ChecklistResponseDTO> updateChecklist(
            @Parameter(description = "Checklist ID", required = true)
            @PathVariable Integer checklistId,
            @Valid @RequestBody ChecklistRequestDTO requestDTO) {

        ChecklistResponseDTO response = checklistService.updateChecklist(checklistId, requestDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * Checklist'in position'ını günceller (sürükle-bırak)
     * Yetkilendirme kontrolü için checklistId kullanılır.
     */
    @PatchMapping("/{checklistId}/position")
    @PreAuthorize("hasPermission(#checklistId, 'update_checklist')")
    @Operation(summary = "Checklist pozisyon güncelle",
            description = "Checklist'in kartaki pozisyonunu günceller")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Position başarıyla güncellendi"),
            @ApiResponse(responseCode = "404", description = "Checklist bulunamadı")
    })
    public ResponseEntity<ChecklistResponseDTO> updateChecklistPosition(
            @Parameter(description = "Checklist ID", required = true)
            @PathVariable Integer checklistId,
            @Parameter(description = "Yeni position değeri", required = true)
            @RequestParam Integer newPosition) {

        ChecklistResponseDTO response = checklistService.updatePosition(checklistId, newPosition);
        return ResponseEntity.ok(response);
    }

    /**
     * Checklist'in ilerleme bilgilerini getirir
     * Yetkilendirme kontrolü için checklistId kullanılır.
     */
    @GetMapping("/{checklistId}/progress")
    @PreAuthorize("hasPermission(#checklistId, 'read_checklist')")
    @Operation(summary = "Checklist ilerlemesi getir",
            description = "Checklist'in tamamlanma oranını ve istatistiklerini getirir")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "İlerleme bilgileri başarıyla getirildi"),
            @ApiResponse(responseCode = "404", description = "Checklist bulunamadı")
    })
    public ResponseEntity<ChecklistProgressDTO> getChecklistProgress(
            @Parameter(description = "Checklist ID", required = true)
            @PathVariable Integer checklistId) {

        ChecklistProgressDTO response = checklistService.getChecklistProgress(checklistId);
        return ResponseEntity.ok(response);
    }

    /**
     * Checklist'i ve tüm item'larını siler
     * Yetkilendirme kontrolü için checklistId kullanılır.
     */
    @DeleteMapping("/{checklistId}")
    @PreAuthorize("hasPermission(#checklistId, 'delete_checklist')")
    @Operation(summary = "Checklist sil",
            description = "Checklist'i ve tüm item'larını kalıcı olarak siler")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Checklist başarıyla silindi"),
            @ApiResponse(responseCode = "404", description = "Checklist bulunamadı")
    })
    public ResponseEntity<Void> deleteChecklist(
            @Parameter(description = "Checklist ID", required = true)
            @PathVariable Integer checklistId) {

        checklistService.deleteChecklist(checklistId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Checklist'e ait tamamlanmış item'ları siler
     * Yetkilendirme kontrolü için checklistId kullanılır.
     */
    @DeleteMapping("/{checklistId}/completed-items")
    @PreAuthorize("hasPermission(#checklistId, 'delete_checklist')")
    @Operation(summary = "Tamamlanmış item'ları sil",
            description = "Checklist'e ait tamamlanmış tüm item'ları siler")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Tamamlanmış item'lar başarıyla silindi"),
            @ApiResponse(responseCode = "404", description = "Checklist bulunamadı")
    })
    public ResponseEntity<Void> deleteCompletedItems(
            @Parameter(description = "Checklist ID", required = true)
            @PathVariable Integer checklistId) {

        checklistService.deleteCompletedItems(checklistId);
        return ResponseEntity.noContent().build();
    }

    // ================================
    // CHECKLIST ITEM ENDPOINT'LERİ
    // ================================

    /**
     * Checklist'e yeni bir item ekler
     * Yetkilendirme kontrolü için checklistId kullanılır.
     */
    @PostMapping("/{checklistId}/items")
    @PreAuthorize("hasPermission(#checklistId, 'create_checklist_item')")
    @Operation(summary = "Checklist item oluştur",
            description = "Belirtilen checklist'e yeni bir item ekler")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Item başarıyla oluşturuldu"),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek verisi"),
            @ApiResponse(responseCode = "404", description = "Checklist bulunamadı")
    })
    public ResponseEntity<ChecklistItemResponseDTO> createChecklistItem(
            @Parameter(description = "Checklist ID", required = true)
            @PathVariable Integer checklistId,
            @Valid @RequestBody ChecklistItemRequestDTO requestDTO) {

        // Checklist ID'yi request DTO'ya ata
        requestDTO.setChecklistId(checklistId);

        ChecklistItemResponseDTO response = checklistItemService.createItem(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Belirli bir checklist item'ını getirir
     * Yetkilendirme kontrolü için itemId kullanılır.
     */
    @GetMapping("/items/{itemId}")
    @PreAuthorize("hasPermission(#itemId, 'read_checklist_item')")
    @Operation(summary = "Checklist item getir",
            description = "ID'si verilen checklist item'ını getirir")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item başarıyla getirildi"),
            @ApiResponse(responseCode = "404", description = "Item bulunamadı")
    })
    public ResponseEntity<ChecklistItemResponseDTO> getChecklistItemById(
            @Parameter(description = "Item ID", required = true)
            @PathVariable Integer itemId) {

        ChecklistItemResponseDTO response = checklistItemService.getItemById(itemId);
        return ResponseEntity.ok(response);
    }

    /**
     * Checklist'e ait tüm item'ları getirir
     * Yetkilendirme kontrolü için checklistId kullanılır.
     */
    @GetMapping("/{checklistId}/items")
    @PreAuthorize("hasPermission(#checklistId, 'read_checklist')")
    @Operation(summary = "Checklist item'larını getir",
            description = "Belirtilen checklist'e ait tüm item'ları getirir")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item'lar başarıyla getirildi"),
            @ApiResponse(responseCode = "404", description = "Checklist bulunamadı")
    })
    public ResponseEntity<List<ChecklistItemResponseDTO>> getChecklistItems(
            @Parameter(description = "Checklist ID", required = true)
            @PathVariable Integer checklistId) {

        List<ChecklistItemResponseDTO> response = checklistItemService.getItemsByChecklistId(checklistId);
        return ResponseEntity.ok(response);
    }

    /**
     * Checklist item'ını günceller
     * Yetkilendirme kontrolü için itemId kullanılır.
     */
    @PutMapping("/items/{itemId}")
    @PreAuthorize("hasPermission(#itemId, 'update_checklist_item')")
    @Operation(summary = "Checklist item güncelle",
            description = "Mevcut checklist item'ını günceller")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item başarıyla güncellendi"),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek verisi"),
            @ApiResponse(responseCode = "404", description = "Item bulunamadı")
    })
    public ResponseEntity<ChecklistItemResponseDTO> updateChecklistItem(
            @Parameter(description = "Item ID", required = true)
            @PathVariable Integer itemId,
            @Valid @RequestBody ChecklistItemRequestDTO requestDTO) {

        ChecklistItemResponseDTO response = checklistItemService.updateItem(itemId, requestDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * Item'ın tamamlanma durumunu değiştirir (toggle)
     * Yetkilendirme kontrolü için itemId kullanılır.
     */
    @PatchMapping("/items/{itemId}/toggle")
    @PreAuthorize("hasPermission(#itemId, 'toggle_checklist_item')")
    @Operation(summary = "Item durumu değiştir",
            description = "Item'ın tamamlanma durumunu değiştirir (toggle)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item durumu başarıyla değiştirildi"),
            @ApiResponse(responseCode = "404", description = "Item bulunamadı")
    })
    public ResponseEntity<ChecklistItemResponseDTO> toggleItemCompletion(
            @Parameter(description = "Item ID", required = true)
            @PathVariable Integer itemId) {

        ChecklistItemResponseDTO response = checklistItemService.toggleCompletion(itemId);
        return ResponseEntity.ok(response);
    }

    /**
     * Item'ın sadece text içeriğini günceller
     * Yetkilendirme kontrolü için itemId kullanılır.
     */
    @PatchMapping("/items/{itemId}/text")
    @PreAuthorize("hasPermission(#itemId, 'update_checklist_item')")
    @Operation(summary = "Item text güncelle",
            description = "Item'ın sadece text içeriğini günceller")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Text başarıyla güncellendi"),
            @ApiResponse(responseCode = "400", description = "Geçersiz text değeri"),
            @ApiResponse(responseCode = "404", description = "Item bulunamadı")
    })
    public ResponseEntity<ChecklistItemResponseDTO> updateItemText(
            @Parameter(description = "Item ID", required = true)
            @PathVariable Integer itemId,
            @Parameter(description = "Yeni text içeriği", required = true)
            @RequestParam String newText) {

        ChecklistItemResponseDTO response = checklistItemService.updateText(itemId, newText);
        return ResponseEntity.ok(response);
    }

    /**
     * Item'ın position'ını günceller (sürükle-bırak)
     * Yetkilendirme kontrolü için itemId kullanılır.
     */
    @PatchMapping("/items/{itemId}/position")
    @PreAuthorize("hasPermission(#itemId, 'update_checklist_item')")
    @Operation(summary = "Item pozisyon güncelle",
            description = "Item'ın checklist içindeki pozisyonunu günceller")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Position başarıyla güncellendi"),
            @ApiResponse(responseCode = "404", description = "Item bulunamadı")
    })
    public ResponseEntity<ChecklistItemResponseDTO> updateItemPosition(
            @Parameter(description = "Item ID", required = true)
            @PathVariable Integer itemId,
            @Parameter(description = "Yeni position değeri", required = true)
            @RequestParam Integer newPosition) {

        ChecklistItemResponseDTO response = checklistItemService.updatePosition(itemId, newPosition);
        return ResponseEntity.ok(response);
    }

    /**
     * Checklist item'ını siler
     * Yetkilendirme kontrolü için itemId kullanılır.
     */
    @DeleteMapping("/items/{itemId}")
    @PreAuthorize("hasPermission(#itemId, 'delete_checklist_item')")
    @Operation(summary = "Checklist item sil",
            description = "Checklist item'ını kalıcı olarak siler")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Item başarıyla silindi"),
            @ApiResponse(responseCode = "404", description = "Item bulunamadı")
    })
    public ResponseEntity<Void> deleteChecklistItem(
            @Parameter(description = "Item ID", required = true)
            @PathVariable Integer itemId) {

        checklistItemService.deleteItem(itemId);
        return ResponseEntity.noContent().build();
    }
}