package com.example.demo.exception;

// List bulunamadığında fırlatılan özel exception sınıfı
public class ListNotFoundException extends RuntimeException {
    // Hata mesajı ile exception oluşturur
    public ListNotFoundException(String message) {
        super(message);
}
}