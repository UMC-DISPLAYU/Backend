package com.example.demo.domain.display.presentation.request;

import com.example.demo.domain.display.application.query.SearchDisplayQuery;
import com.example.demo.domain.display.domain.type.DisplayField;
import com.example.demo.domain.display.domain.type.DisplayRegion;
import com.example.demo.domain.display.domain.type.DisplayType;
import com.example.demo.domain.display.domain.type.SearchDisplayStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record SearchDisplayRequest(
    @Schema(description = "전시명 검색어. title 부분 일치로 검색합니다.", example = "졸업") String searchWord,
    @Schema(
            description =
                "전시 진행 상태. UPCOMING: 예정, ONGOING: 진행 중, ENDED: 종료, "
                    + "CLOSING_SOON: 종료 예정(오늘부터 3일 이내 종료)",
            allowableValues = {"UPCOMING", "ONGOING", "ENDED", "CLOSING_SOON"},
            example = "ONGOING")
        SearchDisplayStatus status,
    @Schema(
            description = "전시 지역. ALL: 전체, SEOUL: 서울, GYEONGGI_INCHEON: 경기/인천, OTHERS: 기타",
            allowableValues = {"ALL", "SEOUL", "GYEONGGI_INCHEON", "OTHERS"},
            example = "SEOUL")
        DisplayRegion region,
    @Schema(
            description =
                "전시 분야. PAINTING: 회화, DESIGN: 디자인, PHOTOGRAPHY: 사진, ARCHITECTURE: 건축, "
                    + "VIDEO: 영상, CRAFTS: 공예, SCULPTURE: 조소, FASHION: 패션, "
                    + "ILLUSTRATION: 일러스트, OTHERS: 기타",
            allowableValues = {
              "PAINTING",
              "DESIGN",
              "PHOTOGRAPHY",
              "ARCHITECTURE",
              "VIDEO",
              "CRAFTS",
              "SCULPTURE",
              "FASHION",
              "ILLUSTRATION",
              "OTHERS"
            },
            example = "DESIGN")
        DisplayField field,
    @Schema(
            description =
                "전시 유형. GRADUATION: 졸업전시, ASSIGNMENTS: 과제전, DEPARTMENTS: 학과전, "
                    + "SMALL_GROUP: 소모임전, INTER_GROUP: 연합전, OTHERS: 기타",
            allowableValues = {
              "GRADUATION",
              "ASSIGNMENTS",
              "DEPARTMENTS",
              "SMALL_GROUP",
              "INTER_GROUP",
              "OTHERS"
            },
            example = "GRADUATION")
        DisplayType type,
    @Schema(description = "마지막으로 조회한 전시 ID. 첫 조회는 0을 전달합니다.", example = "0") @NotNull @Min(0) Long cursor,
    @Schema(description = "조회 개수. 1 이상 100 이하입니다.", example = "20") @NotNull @Min(1) @Max(100) Integer size) {

  public SearchDisplayQuery toQuery() {
    return new SearchDisplayQuery(normalizeSearchWord(), status, region, field, type, cursor, size);
  }

  private String normalizeSearchWord() {
    return searchWord == null ? null : searchWord.trim();
  }
}
