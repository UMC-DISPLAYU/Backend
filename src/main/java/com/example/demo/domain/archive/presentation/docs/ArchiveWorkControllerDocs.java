package com.example.demo.domain.archive.presentation.docs;

import com.example.demo.domain.archive.presentation.response.ArchiveWorkCursorResponse;
import com.example.demo.domain.archive.presentation.response.ArchiveWorkToggleResponse;
import com.example.demo.global.response.ApiResponseBody;
import com.example.demo.global.security.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Archive", description = "개인 아카이브 API")
public interface ArchiveWorkControllerDocs {

  @Operation(summary = "작품 저장", description = "작품을 개인 아카이브에 저장합니다.")
  @SecurityRequirement(name = "Authorization")
  @ApiResponse(
      responseCode = "201",
      description = "작품 저장 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(name = "저장 성공", value = ARCHIVE_WORK_SAVE_SUCCESS_EXAMPLE)))
  @ApiResponse(
      responseCode = "401",
      content =
          @Content(
              examples =
                  @ExampleObject(
                      value =
                          """
                          {
                            "resultType": "FAIL",
                            "success": null,
                            "error": { "code": "UNAUTHORIZED", "message": "인증이 필요합니다.", "details": null },
                            "meta": { "timestamp": "2026-07-13T01:49:28", "path": "/api/v1/archives/artworks/1" }
                          }
                          """)))
  ApiResponseBody<ArchiveWorkToggleResponse> saveArchiveWork(
      @Parameter(description = "작품 ID", example = "1") @PathVariable @Positive Long artworkId,
      AuthUser user,
      HttpServletRequest request);

  @Operation(summary = "작품 저장 취소", description = "개인 아카이브에서 작품 저장을 취소합니다.")
  @SecurityRequirement(name = "Authorization")
  @ApiResponse(
      responseCode = "200",
      description = "작품 저장 취소 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(name = "저장 취소 성공", value = ARCHIVE_WORK_DELETE_SUCCESS_EXAMPLE)))
  @ApiResponse(
      responseCode = "401",
      content =
          @Content(
              examples =
                  @ExampleObject(
                      value =
                          """
                          {
                            "resultType": "FAIL",
                            "success": null,
                            "error": { "code": "UNAUTHORIZED", "message": "인증이 필요합니다.", "details": null },
                            "meta": { "timestamp": "2026-07-13T01:49:28", "path": "/api/v1/archives/artworks/1" }
                          }
                          """)))
  ApiResponseBody<ArchiveWorkToggleResponse> deleteArchiveWork(
      @Parameter(description = "작품 ID", example = "1") @PathVariable @Positive Long artworkId,
      AuthUser user,
      HttpServletRequest request);

  @Operation(summary = "저장된 작품 목록 조회", description = "내가 저장한 작품 목록을 최근 저장한 순으로 커서 기반 페이지네이션 조회합니다.")
  @SecurityRequirement(name = "Authorization")
  @ApiResponse(
      responseCode = "200",
      description = "저장된 작품 목록 조회 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(name = "목록 조회 성공", value = ARCHIVE_WORK_LIST_SUCCESS_EXAMPLE)))
  @ApiResponse(
      responseCode = "401",
      content =
          @Content(
              examples =
                  @ExampleObject(
                      value =
                          """
                          {
                            "resultType": "FAIL",
                            "success": null,
                            "error": { "code": "UNAUTHORIZED", "message": "인증이 필요합니다.", "details": null },
                            "meta": { "timestamp": "2026-07-13T01:49:28", "path": "/api/v1/archives/artworks" }
                          }
                          """)))
  ApiResponseBody<ArchiveWorkCursorResponse> getArchivedWorks(
      @Parameter(description = "마지막으로 조회한 저장 기록 ID. 첫 요청이면 전달하지 않음")
          @RequestParam(required = false)
          @Positive Long cursorId,
      @Parameter(description = "한 번에 불러올 개수") @RequestParam(defaultValue = "10") @Min(1) @Max(50) int size,
      AuthUser user,
      HttpServletRequest request);

  String ARCHIVE_WORK_SAVE_SUCCESS_EXAMPLE =
      """
      {
        "resultType": "SUCCESS",
        "success": {
          "data": {
            "artworkId": 1,
            "isArchived": true
          }
        },
        "error": null,
        "meta": { "timestamp": "2026-07-13T01:49:28", "path": "/api/v1/archives/artworks/1" }
      }
      """;

  String ARCHIVE_WORK_DELETE_SUCCESS_EXAMPLE =
      """
      {
        "resultType": "SUCCESS",
        "success": {
          "data": {
            "artworkId": 1,
            "isArchived": false
          }
        },
        "error": null,
        "meta": { "timestamp": "2026-07-13T01:49:28", "path": "/api/v1/archives/artworks/1" }
      }
      """;

  String ARCHIVE_WORK_LIST_SUCCESS_EXAMPLE =
      """
      {
        "resultType": "SUCCESS",
        "success": {
          "data": {
            "works": [
              {
                "artworkImageUrl": "https://cdn.displayu.co.kr/artworks/1/thumb.jpg",
                "artworkName": "FORM 2026",
                "artistName": "고상준",
                "memo": "이 작품의 색감이 좋았다.",
                "archiveWorkId": 1,
                "artworkId": 1,
                "userId": 1,
                "savedAt": "2026-07-13T01:49:28"
              }
            ],
            "nextCursorId": null,
            "size": 10,
            "hasNext": false
          }
        },
        "error": null,
        "meta": { "timestamp": "2026-07-13T01:49:28", "path": "/api/v1/archives/artworks" }
      }
      """;
}
