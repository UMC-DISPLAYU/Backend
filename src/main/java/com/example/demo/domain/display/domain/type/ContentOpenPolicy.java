package com.example.demo.domain.display.domain.type;

import java.time.LocalDate;

public enum ContentOpenPolicy {
  IMMEDIATELY,
  ON_EXHIBITION;

  public boolean isVisibleOn(DisplayStatus status, LocalDate startDate, LocalDate today) {
    return status == DisplayStatus.PUBLISHED && (this == IMMEDIATELY || !today.isBefore(startDate));
  }
}
