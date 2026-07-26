package com.example.demo.domain.display.presentation.response;

import java.util.List;

public record ReorderDisplayContentsResponse(
    Long displayId, Long categoryId, List<DisplayContentResponse> contents) {}
