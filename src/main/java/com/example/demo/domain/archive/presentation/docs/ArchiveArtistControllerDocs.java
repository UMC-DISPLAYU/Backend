package com.example.demo.domain.archive.presentation.docs;

import com.example.demo.domain.archive.presentation.response.ArchiveArtistCursorResponse;
import com.example.demo.domain.archive.presentation.response.ArchiveArtistToggleResponse;
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
@SecurityRequirement(name = "Authorization")
public interface ArchiveArtistControllerDocs {

  @Operation(summary = "작가 저장", description = "작가를 개인 아카이브에 저장합니다.")
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
                            "success": { "data": { "artistUserId": 1, "isArchived": true } },
                            "error": null,
                            "meta": { "timestamp": "2026-07-13T01:49:28", "path": "/api/v1/archives/artists/1" }
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
                            "meta": { "timestamp": "2026-07-13T01:49:28", "path": "/api/v1/archives/artists/1" }
                          }
                          """)))
  @ApiResponse(
      responseCode = "404",
      description = "존재하지 않는 작가 프로필입니다.",
      content =
          @Content(
              examples =
                  @ExampleObject(
                      value =
                          """
                          {
                            "resultType": "FAIL",
                            "success": null,
                            "error": { "code": "ARTIST_PROFILE_NOT_FOUND", "message": "존재하지 않는 작가 프로필입니다.", "details": null },
                            "meta": { "timestamp": "2026-07-13T01:49:28", "path": "/api/v1/archives/artists/1" }
                          }
                          """)))
  @ApiResponse(
      responseCode = "409",
      description = "이미 저장한 작가입니다.",
      content =
          @Content(
              examples =
                  @ExampleObject(
                      value =
                          """
                          {
                            "resultType": "FAIL",
                            "success": null,
                            "error": { "code": "ALREADY_ARCHIVED_ARTIST", "message": "이미 저장한 작가입니다.", "details": null },
                            "meta": { "timestamp": "2026-07-13T01:49:28", "path": "/api/v1/archives/artists/1" }
                          }
                          """)))
  ApiResponseBody<ArchiveArtistToggleResponse> saveArchiveArtist(
      @Parameter(description = "작가 userId", example = "1") @PathVariable @Positive Long artistId,
      AuthUser user,
      HttpServletRequest request);

  @Operation(summary = "작가 저장 취소", description = "개인 아카이브에서 작가 저장을 취소합니다.")
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
                            "success": { "data": { "artistUserId": 1, "isArchived": false } },
                            "error": null,
                            "meta": { "timestamp": "2026-07-13T01:49:28", "path": "/api/v1/archives/artists/1" }
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
                            "meta": { "timestamp": "2026-07-13T01:49:28", "path": "/api/v1/archives/artists/1" }
                          }
                          """)))
  @ApiResponse(
      responseCode = "404",
      description = "저장된 작가를 찾을 수 없습니다.",
      content =
          @Content(
              examples =
                  @ExampleObject(
                      value =
                          """
                          {
                            "resultType": "FAIL",
                            "success": null,
                            "error": { "code": "ARCHIVE_ARTIST_NOT_FOUND", "message": "저장된 작가를 찾을 수 없습니다.", "details": null },
                            "meta": { "timestamp": "2026-07-13T01:49:28", "path": "/api/v1/archives/artists/1" }
                          }
                          """)))
  ApiResponseBody<ArchiveArtistToggleResponse> deleteArchiveArtist(
      @Parameter(description = "작가 userId", example = "1") @PathVariable @Positive Long artistId,
      AuthUser user,
      HttpServletRequest request);

  @Operation(summary = "저장된 작가 목록 조회", description = "내가 저장한 작가 목록을 최근 저장한 순으로 커서 기반 페이지네이션 조회합니다.")
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
                                "artists": [
                                  {
                                    "profileImageUrl": "https://cdn.displayu.co.kr/artists/5/profile.png",
                                    "artistName": "김지원",
                                    "fields": ["PAINTING", "ILLUSTRATION"],
                                    "artworkCount": 24,
                                    "exhibitionCount": 8,
                                    "archiveArtistId": 2,
                                    "artistId": 5,
                                    "artistUserId": 12,
                                    "userId": 1,
                                    "savedAt": "2026-07-13T01:49:28"
                                  },
                                  {
                                    "profileImageUrl": "https://cdn.displayu.co.kr/artists/3/profile.png",
                                    "artistName": "고상준",
                                    "fields": ["DESIGN"],
                                    "artworkCount": 3,
                                    "exhibitionCount": 1,
                                    "archiveArtistId": 1,
                                    "artistId": 3,
                                    "artistUserId": 8,
                                    "userId": 1,
                                    "savedAt": "2026-07-12T15:10:02"
                                  }
                                ],
                                "nextCursorId": 1,
                                "size": 10,
                                "hasNext": false
                              }
                            },
                            "error": null,
                            "meta": { "timestamp": "2026-07-13T01:49:28", "path": "/api/v1/archives/artists" }
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
                            "meta": { "timestamp": "2026-07-13T01:49:28", "path": "/api/v1/archives/artists" }
                          }
                          """)))
  ApiResponseBody<ArchiveArtistCursorResponse> getArchivedArtists(
      @Parameter(description = "마지막으로 조회한 저장 기록 ID. 첫 요청이면 전달하지 않음")
          @RequestParam(required = false)
          @Positive Long cursorId,
      @Parameter(description = "한 번에 불러올 개수") @RequestParam(defaultValue = "10") @Min(1) @Max(50) int size,
      AuthUser user,
      HttpServletRequest request);
}
