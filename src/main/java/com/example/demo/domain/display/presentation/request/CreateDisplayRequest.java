package com.example.demo.domain.display.presentation.request;

import com.example.demo.domain.display.application.command.CreateDisplayCommand;
import com.example.demo.domain.display.domain.type.ContentOpenPolicy;
import com.example.demo.domain.display.domain.type.DisplayField;
import com.example.demo.domain.display.domain.type.DisplayType;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record CreateDisplayRequest(
    @NotBlank String title,
    @NotBlank String posterImageUrl,
    @NotNull Type type,
    @NotEmpty List<Field> fields,
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

  public CreateDisplayCommand toCommand(Long ownerUserId) {
    return new CreateDisplayCommand(
        ownerUserId,
        title,
        posterImageUrl,
        subtitle,
        description,
        locationName,
        BigDecimal.ZERO,
        BigDecimal.ZERO,
        roadAddress,
        "",
        precautions,
        organization(),
        department(),
        type.toDisplayType(),
        fields.get(0).toDisplayField(),
        startDate,
        endDate,
        openTime,
        closeTime,
        ContentOpenPolicy.IMMEDIATELY,
        ContentOpenPolicy.ON_EXHIBITION);
  }

  @AssertTrue(message = "GRADUATION, TASK 타입은 schoolOrOrganization이 필수입니다.") public boolean isSchoolOrOrganizationValid() {
    return !requiresSchoolInfo() || hasText(schoolOrOrganization);
  }

  @AssertTrue(message = "GRADUATION, TASK 타입은 departmentOrClub이 필수입니다.") public boolean isDepartmentOrClubValid() {
    return !requiresSchoolInfo() || hasText(departmentOrClub);
  }

  @AssertTrue(message = "CLUB, JOINT, ETC 타입은 hostOrganizationName이 필수입니다.") public boolean isHostOrganizationNameValid() {
    return requiresSchoolInfo() || hasText(hostOrganizationName);
  }

  private boolean requiresSchoolInfo() {
    return type == Type.GRADUATION || type == Type.TASK;
  }

  private String organization() {
    return requiresSchoolInfo() ? schoolOrOrganization : hostOrganizationName;
  }

  private String department() {
    return requiresSchoolInfo() ? departmentOrClub : "";
  }

  private static boolean hasText(String value) {
    return value != null && !value.isBlank();
  }

  public enum Type {
    GRADUATION,
    TASK,
    CLUB,
    JOINT,
    ETC;

    private DisplayType toDisplayType() {
      return switch (this) {
        case GRADUATION -> DisplayType.GRADUATION;
        case TASK -> DisplayType.ASSIGNMENTS;
        case CLUB -> DisplayType.SMALL_GROUP;
        case JOINT -> DisplayType.INTER_GROUP;
        case ETC -> DisplayType.OTHERS;
      };
    }
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
    ETC;

    private DisplayField toDisplayField() {
      return switch (this) {
        case PAINTING -> DisplayField.PAINTING;
        case DESIGN -> DisplayField.DESIGN;
        case PHOTOGRAPHY -> DisplayField.PHOTOGRAPHY;
        case ARCHITECTURE -> DisplayField.ARCHITECTURE;
        case MEDIA -> DisplayField.VIDEO;
        case CRAFT -> DisplayField.CRAFTS;
        case SCULPTURE -> DisplayField.SCULPTURE;
        case FASHION -> DisplayField.FASHION;
        case COMPLEX -> DisplayField.INTERDISCIPLINARY;
        case ETC -> DisplayField.OTHERS;
      };
    }
  }
}
