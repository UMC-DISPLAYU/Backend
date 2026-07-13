package com.example.demo.domain.display.presentation.request;

import com.example.demo.domain.display.application.query.SearchDisplayQuery;
import com.example.demo.domain.display.domain.type.DisplayField;
import com.example.demo.domain.display.domain.type.DisplayRegion;
import com.example.demo.domain.display.domain.type.DisplayType;
import com.example.demo.domain.display.domain.type.SearchDisplayStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record SearchDisplayRequest(
    String searchWord,
    SearchDisplayStatus status,
    DisplayRegion region,
    DisplayField field,
    DisplayType type,
    @NotNull @Min(0) Long cursor,
    @NotNull @Min(1) @Max(100) Integer size) {

  public SearchDisplayQuery toQuery() {
    return new SearchDisplayQuery(normalizeSearchWord(), status, region, field, type, cursor, size);
  }

  private String normalizeSearchWord() {
    return searchWord == null ? null : searchWord.trim();
  }
}
