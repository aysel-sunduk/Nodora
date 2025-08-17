package com.example.demo.dto.response;

import lombok.Data;

/**
 * Checklist item'ları için Response DTO sınıfı
 * Tekil checklist item bilgilerini döndürmek için kullanılır
 * Checklist içerisindeki tek bir maddeyi temsil eder
 */
@Data
public class ChecklistItemResponseDTO {

    /**
     * Checklist item'ının benzersiz kimlik numarası
     * Veritabanı tarafından otomatik atanan primary key
     */
    private Integer checklistItemsId;

    /**
     * Checklist item'ının başlığı/açıklaması
     * Kullanıcı tarafından belirlenen item metni
     */
    private String text;

    /**
     * Item'ın tamamlanma durumu
     * true = tamamlandı, false = tamamlanmadı
     */
    private Boolean isCompleted;

    /**
     * Bu item'ın ait olduğu checklist'in ID'si
     * İlişkili checklist'i belirtir
     */
    private Integer checklistId;

    /**
     * Item'ın checklist içerisindeki sıralaması
     * Görüntüleme sırasını belirler
     */
    private Integer position;
}