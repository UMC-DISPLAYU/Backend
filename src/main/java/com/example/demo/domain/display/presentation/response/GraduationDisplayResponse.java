package com.example.demo.domain.display.presentation.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.util.List;

public record GraduationDisplayResponse(List<ExhibitionResponse> exhibitions) {

  public record ExhibitionResponse(
      Long displayId,
      String title,
      String posterImageUrl,
      String schoolDepartmentName,
      LocalDate startedAt,
      LocalDate endedAt,
      long dayLeft,
      @JsonProperty("isArchived") boolean isArchived) {}
}
