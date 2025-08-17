package com.example.demo.exception;

public class CardLabelNotFoundException extends RuntimeException {
    public CardLabelNotFoundException(Integer id) {
        super("CardLabel not found with id: "+id);
}
}