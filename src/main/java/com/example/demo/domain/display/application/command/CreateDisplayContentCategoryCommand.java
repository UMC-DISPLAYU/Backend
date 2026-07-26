package com.example.demo.domain.display.application.command;

public record CreateDisplayContentCategoryCommand(
    Long userId, Long displayId, String name, String description) {}
