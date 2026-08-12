package com.example.demo.domain.display.presentation;

import com.example.demo.domain.display.application.service.GetArtistDisplaysService;
import com.example.demo.domain.display.presentation.mapper.DisplayPresentationMapper;
import com.example.demo.domain.display.presentation.response.MyDisplayListResponse;
import com.example.demo.global.response.ApiResponseBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/display")
@Tag(name = "Display", description = "전시 API")
public class ArtistDisplayController {

  private static final String ARTIST_DISPLAY_SUCCESS_EXAMPLE =
      """
      {
        "resultType": "SUCCESS",
        "success": {
          "data": {
            "createdDisplays": [
              {
                "displayId": 1,
                "title": "디유 졸업전시",
                "displayStatus": "DISPLAYING",
                "startDate": "2026-08-01",
                "endDate": "2026-08-20",
                "school": "디유대학교",
                "department": "디자인학부",
                "placeName": "디유 갤러리",
                "postImageUrl": "https://cdn.displayu.com/posters/main.png"
              }
            ],
            "participatedDisplays": []
          }
        },
        "error": null,
        "meta": {
          "timestamp": "2026-08-12T12:00:00",
          "path": "/api/v1/display/artists/15"
        }
      }
      """;

  private final GetArtistDisplaysService getArtistDisplaysService;
  private final DisplayPresentationMapper displayPresentationMapper;

  @GetMapping("/artists/{userId}")
  @Operation(
      summary = "작가 공개 전시 목록 조회",
      description = "특정 작가가 만들거나 참여한 발행 전시 목록을 display 도메인 기준으로 조회합니다.")
  @ApiResponse(
      responseCode = "200",
      description = "작가 공개 전시 목록 조회 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(name = "조회 성공", value = ARTIST_DISPLAY_SUCCESS_EXAMPLE)))
  public ApiResponseBody<MyDisplayListResponse> getArtistDisplays(
      @Parameter(description = "조회할 사용자 ID", required = true, example = "15") @PathVariable
          Long userId,
      HttpServletRequest request) {
    return ApiResponseBody.success(
        displayPresentationMapper.toResponse(getArtistDisplaysService.getArtistDisplays(userId)),
        request);
  }
}
