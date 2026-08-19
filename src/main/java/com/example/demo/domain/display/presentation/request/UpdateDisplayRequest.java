package com.example.demo.domain.display.presentation.request;

import com.example.demo.domain.display.domain.type.DisplayField;
import com.example.demo.domain.display.domain.type.DisplayType;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record UpdateDisplayRequest(
    @NotNull @Positive Long displayId,
    String title,
    String posterImageUrl,
    DisplayType type,
    List<DisplayField> fields,
    String schoolOrOrganization,
    String departmentOrClub,
    String hostOrganizationName,
    @Size(max = 50) String contract,
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
}
