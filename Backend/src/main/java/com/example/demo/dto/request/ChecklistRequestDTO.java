package com.example.demo.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Checklist oluşturma ve güncelleme işlemleri için kullanılan Request DTO sınıfı
 * Client'tan gelen checklist verilerini alır ve validasyon uygular
 */
@Getter
@Setter
public class ChecklistRequestDTO {

    /**
     * Checklist başlığı
     * Zorunlu alan - 1 ile 15 karakter arasında olmalı
     */
    @NotNull(message = "Title cannot be null")
    @Size(min = 1, max = 15, message = "Title must be between 1 and 15 characters")
    private String title;

    /**
     * Bu checklist'in ait olduğu kartın ID'si
     * URL'den alınır - validation gerekli değil
     */
    private Integer cardId;

    /**
     * Checklist'in kart içerisindeki sıralaması
     * Opsiyonel alan - belirtilmezse varsayılan sıralama uygulanır
     */
    private Integer position;
}