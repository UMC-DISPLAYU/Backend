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
    @Schema(
            description =
                "기존 이미지 전용 대표 URL. posterMedia와 동시에 전달할 수 없습니다. 생략 또는 null은 기존 미디어를 유지합니다.")
        String posterImageUrl,
    DisplayType type,
    @ArraySchema(
            minItems = 1,
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
                      "UX_UI",
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
    @Schema(
            types = {"string", "null"},
            description = "생략 또는 null은 유지, 빈 문자열은 삭제. HTTP(S) URL 최대 255자.")
        @Size(max = 255) @Pattern(
            regexp = "^$|^https?://[^\\s/$.?#].[^\\s]*$",
            flags = Pattern.Flag.CASE_INSENSITIVE)
        String instagramUrl,
    @Schema(
            types = {"string", "null"},
            description = "생략 또는 null은 유지, 빈 문자열은 삭제. HTTP(S) URL 최대 255자.")
        @Size(max = 255) @Pattern(
            regexp = "^$|^https?://[^\\s/$.?#].[^\\s]*$",
            flags = Pattern.Flag.CASE_INSENSITIVE)
        String websiteUrl,
    @Schema(
            types = {"string", "null"},
            description = "도로명 주소. 생략 또는 null은 유지하며 빈 주소는 허용하지 않습니다.")
        @Pattern(regexp = ".*\\S.*", flags = Pattern.Flag.DOTALL) String roadAddress,
    @Schema(
            types = {"number", "null"},
            description = "위도. 생략 또는 null은 유지합니다.")
        @DecimalMin("-90.0") @DecimalMax("90.0") BigDecimal latitude,
    @Schema(
            types = {"number", "null"},
            description = "경도. 생략 또는 null은 유지합니다.")
        @DecimalMin("-180.0") @DecimalMax("180.0") BigDecimal longitude,
    @Schema(
            types = {"string", "null"},
            allowableValues = {"SEOUL", "GYEONGGI_INCHEON", "OTHERS"},
            description = "지역. 생략 또는 null은 유지하며 ALL은 허용하지 않습니다.")
        DisplayRegion region,
    @Schema(
            types = {"array", "null"},
            description = "대표 이미지 외 최대 4장. 생략 또는 null은 유지, 빈 배열은 전체 삭제.")
        @JsonProperty("displayImageUrl")
        @Size(max = 4) List<@NotBlank String> displayImageUrls,
    @Valid @Schema(
            types = {"object", "null"},
            description =
                "원본 이미지 기준 표시 범위. 생략 또는 null이면 기존 범위 유지. 대표 URL을 다른 값으로 바꾸면 초기화. 전체 이미지는 x=0, y=0, w=1, h=1.")
        PosterCropRequest posterCrop,
    @Valid @Schema(
            description =
                "대표 미디어. posterImageUrl과 동시 입력 불가. VIDEO는 썸네일 필수, posterCrop 입력 불가. 대표와 추가 미디어를 합쳐 영상 1개, 미디어 5개까지 허용합니다. 생략 또는 null은 유지하며 대표 미디어 변경 시 기존 크롭을 초기화합니다.")
        DisplayMediaRequest posterMedia,
    @Valid @Schema(
            description = "추가 미디어 최대 4개. displayImageUrl과 동시 입력 불가. 생략 또는 null은 유지, 빈 배열은 전체 삭제.")
        @Size(max = 4) List<@NotNull @Valid DisplayMediaRequest> displayMedia) {

  @Schema(hidden = true)
  @AssertTrue(message = "region은 ALL이 될 수 없습니다.") public boolean isRegionValid() {
    return region != DisplayRegion.ALL;
  }

  @Schema(hidden = true)
  @AssertTrue(message = "fields는 비어 있거나 null 항목을 포함할 수 없습니다.") public boolean isFieldsValid() {
    return fields == null
        || (!fields.isEmpty() && fields.stream().allMatch(field -> field != null));
  }
}
