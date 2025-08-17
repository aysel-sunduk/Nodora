package com.example.demo.dto.response;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate; // Correct import for LocalDate
import java.util.List;

/**
 * Kart bilgilerini client'a döndürmek için kullanılan Response DTO sınıfı
 * Kartın temel bilgileri ile birlikte ilişkili tüm checklist'leri içerir.
 */
@Getter
@Setter
public class CardResponse {

    /**
     * Kartın benzersiz kimlik numarası
     */
    private Integer cardId;

    /**
     * Kart başlığı
     */
    private String title;

    /**
     * Kart açıklaması
     */
    private String description;

    /**
     * Kartın bitiş tarihi
     * java.time.LocalDate kullanıldı
     */
    private LocalDate endingDate; // Düzeltildi

    /**
     * Bu kartın ait olduğu listenin ID'si
     */
    private Integer listId;

    /**
     * Kartın liste içerisindeki sıralaması
     */
    private Integer position;

    /**
     * Kartın oluşturucusu olan üyenin ID'si
     */
    private Integer memberId;

    /**
     * Bu karta ait tüm checklist'lerin listesi
     * Her checklist'in kendi item'larını da içerir.
     */
    private List<ChecklistDetailResponseDTO> checklists; // Düzeltildi
}