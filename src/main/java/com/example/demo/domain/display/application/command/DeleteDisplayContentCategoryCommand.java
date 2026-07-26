package com.example.demo.domain.display.application.command;

public record DeleteDisplayContentCategoryCommand(Long userId, Long displayId, Long categoryId) {}
