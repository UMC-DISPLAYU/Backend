package com.example.demo.domain.display.application.result;

import com.example.demo.domain.display.domain.entity.DisplayContent;
import java.util.Comparator;
import java.util.List;

public record ReorderDisplayContentsResult(
    Long displayId, Long categoryId, List<DisplayContentResult> contents) {

  public static ReorderDisplayContentsResult from(
      Long displayId, Long categoryId, List<DisplayContent> contents) {
    return new ReorderDisplayContentsResult(
        displayId,
        categoryId,
        contents.stream()
            .map(DisplayContentResult::from)
            .sorted(Comparator.comparingInt(DisplayContentResult::sortOrder))
            .toList());
  }
}
