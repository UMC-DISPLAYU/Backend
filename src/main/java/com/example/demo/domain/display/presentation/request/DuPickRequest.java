package com.example.demo.domain.display.presentation.request;

import com.example.demo.domain.display.application.query.DuPickQuery;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record DuPickRequest(@Min(1) Long cursor, @Min(1) @Max(100) Integer size) {

  private static final int DEFAULT_SIZE = 20;

  public DuPickQuery toQuery() {
    return new DuPickQuery(cursor, size == null ? DEFAULT_SIZE : size);
  }
}
