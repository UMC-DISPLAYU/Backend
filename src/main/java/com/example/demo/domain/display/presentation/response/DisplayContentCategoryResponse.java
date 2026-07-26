package com.example.demo.domain.display.presentation.response;

import java.util.List;

public record DisplayContentCategoryResponse(
    Long displayId,
    Long categoryId,
    String name,
    String description,
    int sortOrder,
    List<DisplayContentResponse> contents) {}
