package com.example.demo.domain.display.presentation.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record CreateDisplayRequest(
    @NotBlank String title,
    @NotBlank String posterImageUrl,
    @NotNull Type type,
    @NotEmpty List<Field> fields,
    @NotNull Region region,
    String schoolOrOrganization,
    String departmentOrClub,
    String hostOrganizationName,
    String subtitle,
    String description,
    @NotNull LocalDate startDate,
    @NotNull LocalDate endDate,
    @NotNull LocalTime openTime,
    @NotNull LocalTime closeTime,
    @NotBlank String locationName,
    @NotBlank String roadAddress,
    String precautions) {

  @AssertTrue(message = "GRADUATION, TASK 타입은 schoolOrOrganization이 필수입니다.") public boolean isSchoolOrOrganizationValid() {
    return !requiresSchoolInfo() || hasText(schoolOrOrganization);
  }

  @AssertTrue(message = "GRADUATION, TASK 타입은 departmentOrClub이 필수입니다.") public boolean isDepartmentOrClubValid() {
    return !requiresSchoolInfo() || hasText(departmentOrClub);
  }

  @AssertTrue(message = "CLUB, JOINT, ETC 타입은 hostOrganizationName이 필수입니다.") public boolean isHostOrganizationNameValid() {
    return requiresSchoolInfo() || hasText(hostOrganizationName);
  }

  @AssertTrue(message = "region은 ALL이 될 수 없습니다.") public boolean isRegionValid() {
    return region != Region.ALL;
  }

  private boolean requiresSchoolInfo() {
    return type == Type.GRADUATION || type == Type.TASK;
  }

  private static boolean hasText(String value) {
    return value != null && !value.isBlank();
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

  public enum Region {
    ALL,
    SEOUL,
    GYEONGGI_INCHEON,
    OTHERS
  }
}
