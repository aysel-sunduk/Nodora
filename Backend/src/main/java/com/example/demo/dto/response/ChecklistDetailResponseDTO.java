package com.example.demo.dto.response;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

/**
 * Checklist ve ona ait tüm item'ları birlikte döndürmek için kullanılan detaylı Response DTO sınıfı
 * Checklist bilgileri ile birlikte ilişkili tüm checklist item'larını içerir
 * Genellikle checklist detay sayfası veya tam bilgi gerektiğinde kullanılır
 */
@Getter
@Setter
public class ChecklistDetailResponseDTO {

    /**
     * Checklist'in benzersiz kimlik numarası
     * Veritabanı tarafından otomatik atanan primary key
     */
    private Integer checklistId;

    /**
     * Checklist başlığı
     * Kullanıcı tarafından belirlenen checklist adı
     */
    private String title;

    /**
     * Bu checklist'in ait olduğu kartın ID'si
     * İlişkili kartı belirtir
     */
    private Integer cardId;

    /**
     * Checklist'in kart içerisindeki sıralaması
     * Görüntüleme sırasını belirler
     */
    private Integer position;

    /**
     * Bu checklist'e ait tüm item'ların listesi
     * ChecklistItemResponseDTO nesnelerinden oluşan liste
     * Checklist'in tüm maddelerini içerir
     */
    private List<ChecklistItemResponseDTO>items;
}