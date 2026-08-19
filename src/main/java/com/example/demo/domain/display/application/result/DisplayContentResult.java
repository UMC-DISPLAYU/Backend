package com.example.demo.domain.display.application.result;

import com.example.demo.domain.display.domain.entity.DisplayContent;

public record DisplayContentResult(
    Long categoryId, Long contentId, Long userId, String imageUrl, int sortOrder) {

  public static DisplayContentResult from(DisplayContent content) {
    return new DisplayContentResult(
        content.getCategory().getId(),
        content.getId(),
        content.getUserId() == null ? null : content.getUserId().value(),
        content.getImageUrl(),
        content.getSortOrder());
  }
}
