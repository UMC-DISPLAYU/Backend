package com.example.demo.domain.archive.presentation.docs;

import com.example.demo.domain.archive.presentation.response.ArchiveWorkCursorResponse;
import com.example.demo.domain.archive.presentation.response.ArchiveWorkResponse;
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

  @Operation(summary = "저장된 작품 상세 조회", description = "저장 기록 ID로 저장된 작품 상세를 조회합니다.")
  @SecurityRequirement(name = "Authorization")
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
  ApiResponseBody<ArchiveWorkResponse> getArchiveWorkDetail(
      @Parameter(description = "저장된 작품(아카이브 기록) ID", example = "1") @PathVariable @Positive Long savedArtworkId,
      AuthUser user,
      HttpServletRequest request);

  @Operation(summary = "저장된 작품 목록 조회", description = "내가 저장한 작품 목록을 최근 저장한 순으로 커서 기반 페이지네이션 조회합니다.")
  @SecurityRequirement(name = "Authorization")
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
}
