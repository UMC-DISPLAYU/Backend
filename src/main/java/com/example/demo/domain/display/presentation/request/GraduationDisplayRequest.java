package com.example.demo.domain.display.presentation.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record GraduationDisplayRequest(@Min(1) @Max(100) Integer size) {

  private static final int DEFAULT_SIZE = 20;

  public int requestedSize() {
    return size == null ? DEFAULT_SIZE : size;
  }
}
