package com.example.demo.domain.display.application.result;

import com.example.demo.domain.display.domain.entity.DisplayContentCategory;
import java.util.Comparator;
import java.util.List;

public record DisplayContentCategoryResult(
    Long displayId,
    Long categoryId,
    String name,
    String description,
    int sortOrder,
    List<DisplayContentResult> contents) {

  public static DisplayContentCategoryResult from(DisplayContentCategory category) {
    return new DisplayContentCategoryResult(
        category.getDisplay().getId(),
        category.getId(),
        category.getName(),
        category.getDescription(),
        category.getSortOrder(),
        category.getContents().stream()
            .map(DisplayContentResult::from)
            .sorted(Comparator.comparingInt(DisplayContentResult::sortOrder))
            .toList());
  }
}
