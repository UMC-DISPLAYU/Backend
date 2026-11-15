package com.example.demo.domain.display.application.command;

import java.util.List;

public record CreateDisplayContentCategoryCommand(
    Long userId, Long displayId, String name, String description, List<String> imageUrls) {}
