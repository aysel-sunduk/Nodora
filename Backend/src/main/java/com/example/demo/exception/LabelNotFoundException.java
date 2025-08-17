package com.example.demo.exception;

public class LabelNotFoundException extends RuntimeException {
    public LabelNotFoundException(String message) {
        super(message);
}
}