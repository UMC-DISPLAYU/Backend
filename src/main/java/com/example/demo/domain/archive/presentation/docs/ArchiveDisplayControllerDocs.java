package com.example.demo.domain.archive.presentation.docs;

import com.example.demo.domain.archive.presentation.response.ArchiveDisplayCursorResponse;
import com.example.demo.domain.archive.presentation.response.ArchiveDisplayToggleResponse;
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
public interface ArchiveDisplayControllerDocs {

  @Operation(summary = "전시 저장", description = "전시를 개인 아카이브에 저장합니다.")
  @SecurityRequirement(name = "Authorization")
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
                            "success": { "data": { "exhibitionId": 1, "isArchived": true } },
                            "error": null,
                            "meta": { "timestamp": "2026-07-13T01:49:28", "path": "/api/v1/archives/exhibitions/1" }
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
                            "meta": { "timestamp": "2026-07-13T01:49:28", "path": "/api/v1/archives/exhibitions/1" }
                          }
                          """)))
  ApiResponseBody<ArchiveDisplayToggleResponse> saveArchiveDisplay(
      @Parameter(description = "전시 ID", example = "1") @PathVariable @Positive Long exhibitionId,
      AuthUser user,
      HttpServletRequest request);

  @Operation(summary = "전시 저장 취소", description = "개인 아카이브에서 전시 저장을 취소합니다.")
  @SecurityRequirement(name = "Authorization")
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
                            "success": { "data": { "exhibitionId": 1, "isArchived": false } },
                            "error": null,
                            "meta": { "timestamp": "2026-07-13T01:49:28", "path": "/api/v1/archives/exhibitions/1" }
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
                            "meta": { "timestamp": "2026-07-13T01:49:28", "path": "/api/v1/archives/exhibitions/1" }
                          }
                          """)))
  ApiResponseBody<ArchiveDisplayToggleResponse> deleteArchiveDisplay(
      @Parameter(description = "전시 ID", example = "1") @PathVariable @Positive Long exhibitionId,
      AuthUser user,
      HttpServletRequest request);

  @Operation(summary = "저장된 전시 목록 조회", description = "내가 저장한 전시 목록을 최근 저장한 순으로 커서 기반 페이지네이션 조회합니다.")
  @SecurityRequirement(name = "Authorization")
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
                            "success": {
                              "data": {
                                "displays": [
                                  {
                                    "posterImageUrl": "https://cdn.displayu.co.kr/posters/5.png",
                                    "status": "ENDED",
                                    "title": "syzygy",
                                    "organization": "건국대학교",
                                    "department": "디자인학과",
                                    "startedAt": "2026-05-28",
                                    "endedAt": "2026-06-05",
                                    "location": "건국대학교 디자인관 310관 갤러리",
                                    "archiveDisplayId": 2,
                                    "displayId": 5,
                                    "userId": 1,
                                    "memo": null,
                                    "savedAt": "2026-07-13T01:49:28"
                                  },
                                  {
                                    "posterImageUrl": "https://cdn.displayu.co.kr/posters/3.png",
                                    "status": "ENDED",
                                    "title": "형태의 침묵",
                                    "organization": "중앙대학교",
                                    "department": "디자인학부",
                                    "startedAt": "2026-04-03",
                                    "endedAt": "2026-04-05",
                                    "location": "중앙대학교 310관 갤러리",
                                    "archiveDisplayId": 1,
                                    "displayId": 3,
                                    "userId": 1,
                                    "memo": "여기 조명이 인상적이었다.",
                                    "savedAt": "2026-07-12T15:10:02"
                                  }
                                ],
                                "nextCursorId": 1,
                                "size": 10,
                                "hasNext": false
                              }
                            },
                            "error": null,
                            "meta": { "timestamp": "2026-07-13T01:49:28", "path": "/api/v1/archives/exhibitions" }
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
                            "meta": { "timestamp": "2026-07-13T01:49:28", "path": "/api/v1/archives/exhibitions" }
                          }
                          """)))
  ApiResponseBody<ArchiveDisplayCursorResponse> getArchivedDisplays(
      @Parameter(description = "마지막으로 조회한 저장 기록 ID. 첫 요청이면 전달하지 않음")
          @RequestParam(required = false)
          @Positive Long cursorId,
      @Parameter(description = "한 번에 불러올 개수") @RequestParam(defaultValue = "10") @Min(1) @Max(50) int size,
      AuthUser user,
      HttpServletRequest request);
}
