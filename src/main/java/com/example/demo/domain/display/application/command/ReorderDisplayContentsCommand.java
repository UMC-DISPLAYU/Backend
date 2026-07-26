package com.example.demo.domain.display.application.command;

import java.util.List;

public record ReorderDisplayContentsCommand(
    Long userId, Long displayId, Long categoryId, List<Long> orderedContentIds) {}
