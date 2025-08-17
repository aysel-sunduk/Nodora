package com.example.demo.validation;

import org.springframework.stereotype.Component;

import com.example.demo.dto.request.CardLabelsRequest;

@Component
public class CardLabelsValidation {
    public void validate(CardLabelsRequest dto) {
        if (dto.getCardId() == null) {
            throw new IllegalArgumentException("cardId boş olamaz");
        }
        if (dto.getLabelId() == null) {
            throw new IllegalArgumentException("labelId boş olamaz");
        }
        if (dto.getMemberId() == null) {
            throw new IllegalArgumentException("memberId boş olamaz");
 }
}
}