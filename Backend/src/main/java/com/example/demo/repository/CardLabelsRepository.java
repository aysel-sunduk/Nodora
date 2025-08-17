package com.example.demo.repository;

import com.example.demo.model.card_labels.Card_Labels;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardLabelsRepository extends JpaRepository<Card_Labels,Integer>{
}