package com.example.demo.domain.display.application.command;

public record UpdateDisplayContentCommand(
    Long userId,
    Long displayId,
    Long categoryId,
    Long contentId,
    String imageUrl,
    int width,
    int height) {}
