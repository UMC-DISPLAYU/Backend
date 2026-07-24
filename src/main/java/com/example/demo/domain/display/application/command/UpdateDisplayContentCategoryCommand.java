
package com.example.demo.domain.display.application.command;

public record UpdateDisplayContentCategoryCommand(
    Long userId, Long displayId, Long categoryId, String name, String description) {}
