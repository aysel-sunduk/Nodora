package com.example.demo.dto.response;

import lombok.Getter;
import lombok.Setter;

/**
 * Checklist ilerleme bilgilerini client'a döndürmek için kullanılan Response DTO sınıfı
 *
 * Bu DTO checklist'in tamamlanma durumu, ilerleme yüzdesi ve istatistik bilgilerini içerir.
 * UI'da progress bar ve completion indicator'ları için kullanılır.
 *
 * @author Nodora Team
 * @version 1.0
 */
@Getter
@Setter
public class ChecklistProgressDTO {

    /**
     * Checklist'in benzersiz kimlik numarası
     * Hangi checklist'e ait olduğunu belirtir
     */
    private Integer checklistId;

    /**
     * Checklist başlığı
     * Progress bilgisi ile birlikte gösterilmek için
     */
    private String title;

    /**
     * Toplam item sayısı
     * Checklist'teki tüm madde sayısı
     */
    private long totalItems;

    /**
     * Tamamlanmış item sayısı
     * isCompleted = true olan item sayısı
     */
    private long completedItems;

    /**
     * Bekleyen (tamamlanmamış) item sayısı
     * isCompleted = false olan item sayısı
     */
    private long pendingItems;

    /**
     * Tamamlanma yüzdesi
     * 0-100 arası değer (örn: 75.5)
     */
    private double completionPercentage;

    /**
     * Checklist'in tamamen tamamlanıp tamamlanmadığı
     * true: Tüm item'lar tamamlanmış
     * false: En az bir item tamamlanmamış veya hiç item yok
     */
    private boolean isFullyCompleted;

    /**
     * Checklist'in boş olup olmadığı
     * true: Hiç item yok
     * false: En az bir item var
     */
    private boolean isEmpty;

    /**
     * Progress bilgilerini hesaplar ve ayarlar
     *
     * @param totalItems Toplam item sayısı
     * @param completedItems Tamamlanmış item sayısı
     */
    public void calculateProgress(long totalItems, long completedItems) {
        this.totalItems = totalItems;
        this.completedItems = completedItems;
        this.pendingItems = totalItems - completedItems;
        this.isEmpty = totalItems == 0;
        this.isFullyCompleted = totalItems > 0 && completedItems == totalItems;

        if (totalItems == 0) {
            this.completionPercentage = 0.0;
        } else {
            this.completionPercentage = Math.round((double) completedItems / totalItems * 1000.0) / 10.0;
        }
    }

    /**
     * Default constructor
     */
    public ChecklistProgressDTO() {}

    /**
     * Constructor with basic info
     *
     * @param checklistId Checklist ID
     * @param title Checklist başlığı
     */
    public ChecklistProgressDTO(Integer checklistId, String title) {
        this.checklistId = checklistId;
        this.title =title;
}
}
