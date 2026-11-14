package com.example.demo.domain.display.application.result;

import com.example.demo.domain.display.domain.entity.DisplayContentCategory;
import java.util.Comparator;
import java.util.List;

public record ReorderDisplayContentCategoriesResult(
    Long displayId, List<DisplayContentCategoryResult> categories) {
  public static ReorderDisplayContentCategoriesResult from(
      Long displayId, List<DisplayContentCategory> categories) {
    return new ReorderDisplayContentCategoriesResult(
        displayId,
        categories.stream()
            .map(DisplayContentCategoryResult::from)
            .sorted(Comparator.comparingInt(DisplayContentCategoryResult::sortOrder))
            .toList());
  }
}
