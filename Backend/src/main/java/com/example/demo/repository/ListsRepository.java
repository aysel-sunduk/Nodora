package com.example.demo.repository;

import com.example.demo.model.lists.Lists;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// Lists tablosu için temel CRUD işlemlerini sağlayan repository arayüzü
@Repository
public interface ListsRepository extends JpaRepository<Lists, Integer> {

    /**
     * Belirli bir boardId'ye sahip tüm listeleri, position alanına göre sıralayarak getirir.
     *
     * @param boardId Sorgulanacak board'un kimlik numarası
     * @return Belirtilen board'a ait listelerin listesi
     */
    List<Lists> findByBoardIdOrderByPosition(Integer boardId);
}