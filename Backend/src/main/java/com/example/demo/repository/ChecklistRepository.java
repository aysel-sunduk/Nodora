package com.example.demo.repository;

import com.example.demo.model.checklist.Checklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Checklist entity'si için veri erişim katmanı repository interface'i
 *
 * Spring Data JPA kullanarak temel CRUD işlemleri ve özel sorgular sağlar.
 * JpaRepository'den miras alarak otomatik method implementasyonları kazanır.
 * CardController tarafından kullanılır.
 *
 * @author Nodora Team
 * @version 1.0
 */
@Repository
public interface ChecklistRepository extends JpaRepository<Checklist, Integer> {

    /**
     * Belirli bir karta ait tüm checklist'leri position'a göre sıralı şekilde getirir
     *
     * @param cardId Checklist'leri getirilecek kartın kimlik numarası
     * @return List<Checklist> Karta ait checklist'lerin position'a göre sıralı listesi
     *
     * @apiNote Bu method en sık kullanılan sorgulardan biridir
     *          Position'a göre sıralama UI'da doğru sıralamayı sağlar
     */
    List<Checklist> findByCardIdOrderByPositionAsc(Integer cardId);

    /**
     * Belirli bir karta ait checklist'lerin varlığını kontrol eder
     *
     * @param cardId Kontrol edilecek kartın kimlik numarası
     * @return boolean Karta ait checklist varsa true, yoksa false
     *
     * @apiNote Kart silinmeden önce checklist kontrolü için kullanılabilir
     */
    boolean existsByCardId(Integer cardId);

    /**
     * Belirli bir karta ait checklist sayısını döner
     *
     * @param cardId Sayısı alınacak kartın kimlik numarası
     * @return int Karta ait checklist sayısı
     *
     * @apiNote İstatistik ve limit kontrolü için kullanılabilir
     */
    int countByCardId(Integer cardId);

    /**
     * Belirli bir karta ait en büyük position değerini getirir
     *
     * @param cardId Position değeri alınacak kartın kimlik numarası
     * @return Optional<Integer> En büyük position değeri (checklist yoksa empty)
     *
     * @apiNote Yeni checklist eklenirken position değeri belirlemek için kullanılır
     */
    @Query("SELECT MAX(c.position) FROM Checklist c WHERE c.cardId = :cardId")
    Optional<Integer> findMaxPositionByCardId(@Param("cardId") Integer cardId);
}
