package com.example.demo.domain.display.application.result;

import com.example.demo.domain.display.domain.entity.DisplayContent;

public record DisplayContentResult(
    Long categoryId, Long contentId, String imageUrl, int sortOrder) {

  public static DisplayContentResult from(DisplayContent content) {
    return new DisplayContentResult(
        content.getCategory().getId(),
        content.getId(),
        content.getImageUrl(),
        content.getSortOrder());
  }
}
