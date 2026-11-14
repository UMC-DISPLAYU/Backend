package com.example.demo.domain.display.presentation.response;

import java.util.List;

public record ReorderDisplayContentCategoriesResponse(
    Long displayId, List<DisplayContentCategoryResponse> categories) {}
