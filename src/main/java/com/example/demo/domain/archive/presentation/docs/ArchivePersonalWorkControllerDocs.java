package com.example.demo.domain.archive.presentation.docs;

import com.example.demo.domain.archive.presentation.response.ArchivePersonalWorkToggleResponse;
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
import jakarta.validation.constraints.Positive;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "Archive", description = "개인 아카이브 API")
@SecurityRequirement(name = "Authorization")
public interface ArchivePersonalWorkControllerDocs {

  @Operation(summary = "개인 작품 저장", description = "작가 프로필의 개인 작품을 개인 아카이브에 저장합니다.")
  @ApiResponse(
      responseCode = "201",
      content =
          @Content(
              examples =
                  @ExampleObject(
                      value =
                          """
                          {
                            "resultType": "SUCCESS",
                            "success": { "data": { "personalArtworkId": 1, "isArchived": true } },
                            "error": null,
                            "meta": { "timestamp": "2026-08-11T01:49:28", "path": "/api/v1/archives/personal-artworks/1" }
                          }
                          """)))
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
                            "meta": { "timestamp": "2026-08-11T01:49:28", "path": "/api/v1/archives/personal-artworks/1" }
                          }
                          """)))
  @ApiResponse(
      responseCode = "404",
      description = "존재하지 않는 개인 작품입니다.",
      content =
          @Content(
              examples =
                  @ExampleObject(
                      value =
                          """
                          {
                            "resultType": "FAIL",
                            "success": null,
                            "error": { "code": "PERSONAL_ARTWORK_NOT_FOUND", "message": "존재하지 않는 개인 작품입니다.", "details": null },
                            "meta": { "timestamp": "2026-08-11T01:49:28", "path": "/api/v1/archives/personal-artworks/1" }
                          }
                          """)))
  @ApiResponse(
      responseCode = "409",
      description = "이미 저장한 개인 작품입니다.",
      content =
          @Content(
              examples =
                  @ExampleObject(
                      value =
                          """
                          {
                            "resultType": "FAIL",
                            "success": null,
                            "error": { "code": "ALREADY_ARCHIVED_PERSONAL_WORK", "message": "이미 저장한 개인 작품입니다.", "details": null },
                            "meta": { "timestamp": "2026-08-11T01:49:28", "path": "/api/v1/archives/personal-artworks/1" }
                          }
                          """)))
  ApiResponseBody<ArchivePersonalWorkToggleResponse> saveArchivePersonalWork(
      @Parameter(description = "개인 작품 ID", example = "1") @PathVariable @Positive Long personalArtworkId,
      AuthUser user,
      HttpServletRequest request);

  @Operation(summary = "개인 작품 저장 취소", description = "개인 아카이브에서 개인 작품 저장을 취소합니다.")
  @ApiResponse(
      responseCode = "200",
      content =
          @Content(
              examples =
                  @ExampleObject(
                      value =
                          """
                          {
                            "resultType": "SUCCESS",
                            "success": { "data": { "personalArtworkId": 1, "isArchived": false } },
                            "error": null,
                            "meta": { "timestamp": "2026-08-11T01:49:28", "path": "/api/v1/archives/personal-artworks/1" }
                          }
                          """)))
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
                            "meta": { "timestamp": "2026-08-11T01:49:28", "path": "/api/v1/archives/personal-artworks/1" }
                          }
                          """)))
  @ApiResponse(
      responseCode = "404",
      description = "저장된 개인 작품을 찾을 수 없습니다.",
      content =
          @Content(
              examples =
                  @ExampleObject(
                      value =
                          """
                          {
                            "resultType": "FAIL",
                            "success": null,
                            "error": { "code": "ARCHIVE_PERSONAL_WORK_NOT_FOUND", "message": "저장된 개인 작품을 찾을 수 없습니다.", "details": null },
                            "meta": { "timestamp": "2026-08-11T01:49:28", "path": "/api/v1/archives/personal-artworks/1" }
                          }
                          """)))
  ApiResponseBody<ArchivePersonalWorkToggleResponse> deleteArchivePersonalWork(
      @Parameter(description = "개인 작품 ID", example = "1") @PathVariable @Positive Long personalArtworkId,
      AuthUser user,
      HttpServletRequest request);
}
