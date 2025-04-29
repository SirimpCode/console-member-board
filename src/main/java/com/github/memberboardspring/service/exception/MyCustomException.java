package com.github.memberboardspring.service.exception;

public class MyCustomException extends RuntimeException {
    MyCustomException(String message) {
        super(message);
    }
}
