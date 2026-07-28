package com.example.demo.domain.display.application.command;

public record CreateDisplayContentCommand(
    Long userId, Long displayId, Long categoryId, String imageUrl, int width, int height) {}
