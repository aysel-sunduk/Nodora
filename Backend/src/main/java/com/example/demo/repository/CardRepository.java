package com.example.demo.repository;

import com.example.demo.model.cards.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<Card, Integer> {

    // Belirli bir listeye ait tüm kartları getirir.
    List<Card> findByListId(Integer listId);

    // Belirli bir üyeye ait tüm kartları getirir.
    List<Card> findByMemberId(Integer memberId);

    /**
     * Belirli bir listeye ait tüm kartları, position alanına göre sıralayarak getirir.
     * Bu metot, Service katmanında kullandığımız metot ile uyumlu olacaktır.
     *
     * @param listId Sorgulanacak list'in kimlik numarası
     * @return Belirtilen list'e ait kartların pozisyona göre sıralı listesi
     */
    List<Card> findByListIdOrderByPosition(Integer listId);
}