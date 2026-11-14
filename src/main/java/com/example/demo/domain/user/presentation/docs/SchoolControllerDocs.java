package com.example.demo.domain.user.presentation.docs;

import com.example.demo.domain.user.presentation.response.SchoolSearchResponse;
import com.example.demo.global.response.ApiResponseBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@Tag(name = "School", description = "대학교 검색 API")
public interface SchoolControllerDocs {

  @Operation(
      summary = "대학교 검색",
      description =
          """
          서버가 관리하는 대학교 목록에서 표시명 기준으로 부분 일치 검색합니다.

          - 검색어의 앞뒤 공백은 제거합니다.
          - 영문 검색은 대소문자를 구분하지 않습니다.
          - 검색 결과는 학교명 오름차순으로 반환합니다.
          - 빈 검색어 또는 검색 결과가 없는 경우 빈 배열을 반환합니다.
          """)
  @ApiResponse(
      responseCode = "200",
      description = "대학교 검색 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(name = "검색 성공", value = SEARCH_SUCCESS_EXAMPLE)))
  ApiResponseBody<List<SchoolSearchResponse>> search(
      @Parameter(description = "검색할 대학교명의 전체 또는 일부", example = "서울") String keyword,
      HttpServletRequest httpRequest);

  String SEARCH_SUCCESS_EXAMPLE =
      """
      {
        "resultType": "SUCCESS",
        "success": { "data": [{ "name": "서울대학교" }, { "name": "서울시립대학교" }] },
        "error": null,
        "meta": { "timestamp": "2026-11-14T04:13:15.651", "path": "/api/v1/schools" }
      }
      """;
}
