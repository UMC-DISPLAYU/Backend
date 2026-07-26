package com.example.demo.domain.display.application.command;

public record DeleteDisplayContentCommand(
    Long userId, Long displayId, Long categoryId, Long contentId) {}
