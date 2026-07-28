package com.example.demo.domain.memo.presentation.docs;

import com.example.demo.domain.memo.presentation.request.MemoRequest;
import com.example.demo.domain.memo.presentation.response.MemoResponse;
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
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Memo", description = "개인 아카이브 메모 API")
@SecurityRequirement(name = "Authorization")
public interface MemoControllerDocs {

  @Operation(summary = "전시 메모 작성/수정", description = "저장된 전시 1건당 메모를 하나만 작성합니다. 이미 있으면 덮어씁니다.")
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
                                "memoId": 1,
                                "archiveDisplayId": 12,
                                "archiveWorkId": null,
                                "content": "여기 조명이 인상적이었다.",
                                "visitDate": "2026-07-16",
                                "createdAt": "2026-07-16T10:00:00",
                                "updatedAt": "2026-07-16T10:00:00"
                              }
                            },
                            "error": null,
                            "meta": { "timestamp": "2026-07-16T10:00:00", "path": "/api/v1/archives/exhibitions/12/memo" }
                          }
                          """)))
  ApiResponseBody<MemoResponse> upsertExhibitionMemo(
      @Parameter(description = "저장된 전시(ArchiveDisplay) ID", example = "1") @PathVariable @Positive Long archiveDisplayId,
      @Valid @RequestBody MemoRequest request,
      AuthUser user,
      HttpServletRequest httpRequest);

  @Operation(summary = "전시 메모 삭제", description = "저장된 전시에 작성한 메모를 삭제합니다.")
  ApiResponseBody<Void> deleteExhibitionMemo(
      @Parameter(description = "저장된 전시(ArchiveDisplay) ID", example = "1") @PathVariable @Positive Long archiveDisplayId,
      AuthUser user,
      HttpServletRequest httpRequest);

  @Operation(summary = "작품 메모 작성/수정", description = "저장된 작품 1건당 메모를 하나만 작성합니다. 이미 있으면 덮어씁니다.")
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
                                "memoId": 2,
                                "archiveDisplayId": null,
                                "archiveWorkId": 7,
                                "content": "이 작품의 색감이 좋았다.",
                                "visitDate": "2026-07-16",
                                "createdAt": "2026-07-16T10:00:00",
                                "updatedAt": "2026-07-16T10:00:00"
                              }
                            },
                            "error": null,
                            "meta": { "timestamp": "2026-07-16T10:00:00", "path": "/api/v1/archives/artworks/7/memo" }
                          }
                          """)))
  ApiResponseBody<MemoResponse> upsertArtworkMemo(
      @Parameter(description = "저장된 작품(ArchiveWork) ID", example = "1") @PathVariable @Positive Long archiveWorkId,
      @Valid @RequestBody MemoRequest request,
      AuthUser user,
      HttpServletRequest httpRequest);

  @Operation(summary = "작품 메모 삭제", description = "저장된 작품에 작성한 메모를 삭제합니다.")
  ApiResponseBody<Void> deleteArtworkMemo(
      @Parameter(description = "저장된 작품(ArchiveWork) ID", example = "1") @PathVariable @Positive Long archiveWorkId,
      AuthUser user,
      HttpServletRequest httpRequest);
}
