package com.example.demo.domain.display.presentation.response;

import java.time.LocalDate;
import java.util.List;

public record MyDisplayListResponse(
    List<MyDisplayResponse> createdDisplays, List<MyDisplayResponse> participatedDisplays) {

  public record MyDisplayResponse(
      Long displayId,
      String title,
      String displayStatus,
      LocalDate startDate,
      LocalDate endDate,
      String school,
      String department,
      String placeName,
      String postImageUrl) {}
}
