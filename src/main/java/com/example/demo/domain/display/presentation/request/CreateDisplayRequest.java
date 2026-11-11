package com.example.demo.domain.display.presentation.request;

import com.example.demo.domain.display.domain.type.DisplayField;
import com.example.demo.domain.display.domain.type.DisplayType;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record CreateDisplayRequest(
    @NotBlank String title,
    @NotBlank String posterImageUrl,
    @JsonProperty("displayImageUrl") @Size(max = 4) List<@NotBlank String> displayImageUrls,
    @NotNull DisplayType type,
    @NotEmpty @ArraySchema(
            schema =
                @Schema(
                    allowableValues = {
                      "PAINTING",
                      "SCULPTURE",
                      "PHOTOGRAPHY",
                      "GRAPHIC",
                      "BRANDING",
                      "PRODUCT",
                      "VIDEO",
                      "SPACE",
                      "ARCHITECTURE",
                      "UXUI",
                      "CRAFTS",
                      "OTHERS"
                    }))
        List<DisplayField> fields,
    @NotNull Region region,
    @NotBlank String schoolOrOrganization,
    String departmentOrClub,
    String qnaAccount,
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
    String precautions,
    @Schema(
            description = "선택 입력 HTTP(S) URL. 생략 또는 null 허용, 빈 문자열 불가, 최대 255자.",
            types = {"string", "null"},
            maxLength = 255)
        @Size(max = 255) @Pattern(regexp = "^https?://[^\\s/$.?#].[^\\s]*$", flags = Pattern.Flag.CASE_INSENSITIVE) String instagramUrl,
    @Schema(
            description = "선택 입력 HTTP(S) URL. 생략 또는 null 허용, 빈 문자열 불가, 최대 255자.",
            types = {"string", "null"},
            maxLength = 255)
        @Size(max = 255) @Pattern(regexp = "^https?://[^\\s/$.?#].[^\\s]*$", flags = Pattern.Flag.CASE_INSENSITIVE) String websiteUrl,
    @Valid @Schema(
            types = {"object", "null"},
            description = "원본 이미지 기준 표시 범위. 생략 또는 null이면 생성 시 조정 없음, 수정 시 기존 값 유지.")
        PosterCropRequest posterCrop) {

  @AssertTrue(message = "GRADUATION, ASSIGNMENTS 타입은 departmentOrClub이 필수입니다.") public boolean isDepartmentOrClubValid() {
    return !requiresSchoolInfo() || hasText(departmentOrClub);
  }

  @AssertTrue(message = "fields는 신규 전시 분야만 선택할 수 있습니다.") public boolean isFieldsValid() {
    return fields == null
        || fields.stream().allMatch(field -> field != null && field.isSelectable());
  }

  @AssertTrue(message = "region은 ALL이 될 수 없습니다.") public boolean isRegionValid() {
    return region != Region.ALL;
  }

  private boolean requiresSchoolInfo() {
    return type == DisplayType.GRADUATION || type == DisplayType.ASSIGNMENTS;
  }

  private static boolean hasText(String value) {
    return value != null && !value.isBlank();
  }

  public enum Region {
    ALL,
    SEOUL,
    GYEONGGI_INCHEON,
    OTHERS
  }
}
