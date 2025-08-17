package com.example.demo.dto.response;

import lombok.Getter;
import lombok.Setter;

/**
 * Checklist bilgilerini client'a döndürmek için kullanılan Response DTO sınıfı
 * Temel checklist bilgilerini içerir
 */
@Getter
@Setter
public class ChecklistResponseDTO {

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
}