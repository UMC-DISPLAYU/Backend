package com.example.demo.domain.display.presentation.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;

public record ReorderDisplayContentCategoriesRequest(
    @NotNull List<@NotNull @Positive Long> orderedCategoryIds) {}
