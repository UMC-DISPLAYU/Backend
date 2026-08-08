package com.example.demo.domain.display.presentation.response;

public record DisplayContentResponse(
    Long categoryId, Long contentId, String imageUrl, int sortOrder) {}
