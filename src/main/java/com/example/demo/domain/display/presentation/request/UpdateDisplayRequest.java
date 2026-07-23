package com.example.demo.domain.display.presentation.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record UpdateDisplayRequest(
    @NotNull @Positive Long displayId,
    String title,
    String posterImageUrl,
    Type type,
    List<Field> fields,
    String schoolOrOrganization,
    String departmentOrClub,
    String hostOrganizationName,
    String subtitle,
    String description,
    LocalDate startDate,
    LocalDate endDate,
    LocalTime openTime,
    LocalTime closeTime,
    String placeName,
    String precautions) {

  @AssertTrue(message = "fields는 비어 있을 수 없습니다.") public boolean isFieldsValid() {
    return fields == null || !fields.isEmpty();
  }

  public enum Type {
    GRADUATION,
    TASK,
    CLUB,
    JOINT,
    ETC
  }

  public enum Field {
    PAINTING,
    DESIGN,
    PHOTOGRAPHY,
    ARCHITECTURE,
    MEDIA,
    CRAFT,
    SCULPTURE,
    FASHION,
    COMPLEX,
    ETC
  }
}
