package com.example.demo.domain.display.presentation.request;

import com.example.demo.domain.display.domain.type.DisplayField;
import com.example.demo.domain.display.domain.type.DisplayRegion;
import com.example.demo.domain.display.domain.type.DisplayType;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record UpdateDisplayRequest(
    @NotNull @Positive Long displayId,
    String title,
    String posterImageUrl,
    DisplayType type,
    @ArraySchema(
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
    String precautions,
    @Schema(description = "생략 또는 null은 유지, 빈 문자열은 삭제. HTTP(S) URL 최대 255자.")
        @Size(max = 255) @Pattern(
            regexp = "^$|^https?://[^\\s/$.?#].[^\\s]*$",
            flags = Pattern.Flag.CASE_INSENSITIVE)
        String instagramUrl,
    @Schema(description = "생략 또는 null은 유지, 빈 문자열은 삭제. HTTP(S) URL 최대 255자.")
        @Size(max = 255) @Pattern(
            regexp = "^$|^https?://[^\\s/$.?#].[^\\s]*$",
            flags = Pattern.Flag.CASE_INSENSITIVE)
        String websiteUrl,
    @Pattern(regexp = ".*\\S.*", flags = Pattern.Flag.DOTALL) String roadAddress,
    @DecimalMin("-90.0") @DecimalMax("90.0") BigDecimal latitude,
    @DecimalMin("-180.0") @DecimalMax("180.0") BigDecimal longitude,
    DisplayRegion region,
    @Schema(description = "대표 이미지 외 최대 4장. 생략 또는 null은 유지, 빈 배열은 전체 삭제.")
        @JsonProperty("displayImageUrl")
        @Size(max = 4) List<@NotBlank String> displayImageUrls,
    @Valid @Schema(
            types = {"object", "null"},
            description = "원본 이미지 기준 표시 범위. 생략 또는 null이면 생성 시 조정 없음, 수정 시 기존 값 유지.")
        PosterCropRequest posterCrop) {

  @AssertTrue(message = "region은 ALL이 될 수 없습니다.") public boolean isRegionValid() {
    return region != DisplayRegion.ALL;
  }

  @AssertTrue(message = "fields는 비어 있을 수 없으며 신규 전시 분야만 선택할 수 있습니다.") public boolean isFieldsValid() {
    return fields == null
        || (!fields.isEmpty()
            && fields.stream().allMatch(field -> field != null && field.isSelectable()));
  }
}
