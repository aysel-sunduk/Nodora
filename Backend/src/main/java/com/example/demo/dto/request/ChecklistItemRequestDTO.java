package com.example.demo.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Checklist item (madde) oluşturma ve güncelleme işlemleri için kullanılan Request DTO sınıfı
 * Client'tan gelen checklist item verilerini alır ve validasyon uygular
 */
@Getter
@Setter
public class ChecklistItemRequestDTO {

    /**
     * Bu item'ın ait olduğu checklist'in ID'si
     * URL'den alınır - validation gerekli değil
     */
    private Integer checklistId;

    /**
     * Checklist item'ının metin içeriği
     * Zorunlu alan - 1 ile 25 karakter arasında olmalı
     */
    @NotNull(message = "Text cannot be null")
    @Size(min = 1, max = 25, message = "Text must be between 1 and 25 characters")
    private String text;

    /**
     * Item'ın tamamlanma durumu
     * Varsayılan değer: false (tamamlanmamış)
     * Opsiyonel alan - belirtilmezse false olarak atanır
     */
    private Boolean isCompleted = false;

    /**
     * Item'ın checklist içerisindeki sıralaması
     * Opsiyonel alan - belirtilmezse varsayılan sıralama uygulanır
     */
    private Integer position;
}