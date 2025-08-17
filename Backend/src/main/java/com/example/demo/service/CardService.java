package com.example.demo.service;

import com.example.demo.dto.request.CardRequest;
import com.example.demo.dto.response.CardResponse;

import java.util.List;

public interface CardService {
    // Controller'dan DTO alıp DTO dönecek. memberId servis katmanında alınacak.
    CardResponse createCard(CardRequest request);

    // DTO dönecek
    CardResponse getCardResponseById(Integer id);

    // Bir listeye ait kartları dönecek
    List<CardResponse> getCardsByListId(Integer listId);

    // Controller'dan DTO alıp DTO dönecek.
    CardResponse updateCard(Integer id, CardRequest request);

    // Sadece ID ile işlem yapacak
    void deleteCard(Integer id);
}