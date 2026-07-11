package com.example.demo.global.response;

public record ErrorResponse(String code, String message, Object details) {}
