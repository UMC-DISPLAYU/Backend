package com.example.demo.domain.display.presentation.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record CreateDisplayRequest(
    @NotBlank String title,
    @NotBlank String posterImageUrl,
    @NotNull Type type,
    @NotEmpty List<Field> fields,
    @NotNull Region region,
    @NotBlank String schoolOrOrganization,
    String departmentOrClub,
    @NotBlank String qnaAccount,
    @NotBlank String displayNickname,
    @Size(max = 50) String contract,
    String subtitle,
    String description,
    @NotNull LocalDate startDate,
    @NotNull LocalDate endDate,
    @NotNull LocalTime openTime,
    @NotNull LocalTime closeTime,
    @NotBlank String locationName,
    @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") BigDecimal latitude,
    @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") BigDecimal longitude,
    @NotBlank String roadAddress,
    String precautions) {

  @AssertTrue(message = "GRADUATION, TASK 타입은 departmentOrClub이 필수입니다.") public boolean isDepartmentOrClubValid() {
    return !requiresSchoolInfo() || hasText(departmentOrClub);
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
