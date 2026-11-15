package com.example.demo.domain.display.application.command;

import java.util.List;

public record ReorderDisplayContentCategoriesCommand(
    Long userId, Long displayId, List<Long> orderedCategoryIds) {}
