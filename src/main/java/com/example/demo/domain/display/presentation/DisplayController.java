package com.example.demo.domain.display.presentation;

import com.example.demo.domain.display.application.command.CreateDisplayService;
import com.example.demo.domain.display.application.query.GetDisplayDetailService;
import com.example.demo.domain.display.application.result.DisplayDetailResult;
import com.example.demo.domain.display.presentation.mapper.DisplayPresentationMapper;
import com.example.demo.domain.display.presentation.request.CreateDisplayRequest;
import com.example.demo.domain.display.presentation.response.DisplayDetailResponse;
import com.example.demo.global.response.ApiResponseBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Display", description = "전시 API")
public class DisplayController {

  private static final Long TEMP_OWNER_USER_ID = 1L;

  private final CreateDisplayService createDisplayService;
  private final GetDisplayDetailService getDisplayDetailService;
  private final DisplayPresentationMapper mapper;

  public DisplayController(
      CreateDisplayService createDisplayService,
      GetDisplayDetailService getDisplayDetailService,
      DisplayPresentationMapper mapper) {
    this.createDisplayService = createDisplayService;
    this.getDisplayDetailService = getDisplayDetailService;
    this.mapper = mapper;
  }

  @PostMapping("/api/v1/display")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "전시 생성", description = "전시 기본 정보와 일정/장소 정보를 생성합니다.")
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      description = "전시 생성 요청",
      required = true,
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Display create request",
                      value =
                          """
                          {
                            "title": "FORM 2026",
                            "posterImageUrl": "https://cdn...",
                            "type": "GRADUATION",
                            "fields": ["DESIGN", "MEDIA"],
                            "schoolOrOrganization": "중앙대학교",
                            "departmentOrClub": "디자인학부",
                            "hostOrganizationName": null,
                            "subtitle": "중앙대학교 디자인학부 졸업전시",
                            "description": "디자인학부 학생들의...",
                            "startDate": "2026-05-28",
                            "endDate": "2026-06-05",
                            "openTime": "10:00",
                            "closeTime": "18:00",
                            "locationName": "중앙대학교 안성캠퍼스 301관 대전시실 2층",
                            "roadAddress": "경기도 안성시 대덕면 서동대로 4726",
                            "precautions": "전시장 내 음료 반입 금지"
                          }
                          """)))
  @ApiResponse(
      responseCode = "201",
      description = "전시 생성 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Display create success",
                      value =
                          """
                          {
                            "resultType": "SUCCESS",
                            "success": {
                              "data": {
                                "displayId": 1,
                                "ownerUserId": 1,
                                "title": "FORM 2026",
                                "subtitle": "중앙대학교 디자인학부 졸업전시",
                                "content": "디자인학부 학생들의...",
                                "location": {
                                  "placeName": "중앙대학교 안성캠퍼스 301관 대전시실 2층",
                                  "latitude": 0,
                                  "longitude": 0
                                },
                                "qnaAccount": "",
                                "note": "전시장 내 음료 반입 금지",
                                "organization": "중앙대학교",
                                "department": "디자인학부",
                                "displayType": "GRADUATION",
                                "displayFields": ["DESIGN", "VIDEO"],
                                "period": {
                                  "startDate": "2026-05-28",
                                  "endDate": "2026-06-05",
                                  "startTime": "10:00:00",
                                  "endTime": "18:00:00"
                                },
                                "artworkContentOpen": "IMMEDIATELY",
                                "exhibitionContentOpen": "ON_EXHIBITION",
                                "status": "DRAFT",
                                "invitationToken": null,
                                "invitationDisabledAt": null,
                                "images": [
                                  {
                                    "imageId": 1,
                                    "imageUrl": "https://cdn...",
                                    "imageType": "MAIN",
                                    "width": 1,
                                    "height": 1,
                                    "sortOrder": 0
                                  }
                                ],
                                "contentCategories": [],
                                "teamMembers": [],
                                "invitations": []
                              }
                            },
                            "error": null,
                            "meta": {
                              "timestamp": "2026-07-09T19:55:00",
                              "path": "/api/v1/display"
                            }
                          }
                          """)))
  public ApiResponseBody<DisplayDetailResponse> createDisplay(
      @Valid @RequestBody CreateDisplayRequest createDisplayRequest, HttpServletRequest request) {
    Long displayId =
        createDisplayService
            .createDisplay(createDisplayRequest.toCommand(TEMP_OWNER_USER_ID))
            .displayId();
    DisplayDetailResult result = getDisplayDetailService.getDisplayDetail(displayId);
    return ApiResponseBody.success(mapper.toResponse(result), request);
  }

  @GetMapping("/api/v1/display/{displayId}")
  @Operation(summary = "전시 상세 조회", description = "displayId에 해당하는 전시의 전체 상세 데이터를 조회합니다.")
  @ApiResponse(
      responseCode = "200",
      description = "전시 상세 조회 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Display detail success",
                      value =
                          """
                          {
                            "resultType": "SUCCESS",
                            "success": {
                              "data": {
                                "displayId": 1,
                                "ownerUserId": 10,
                                "title": "2026 졸업 전시",
                                "subtitle": "DisplayU Graduation Exhibition",
                                "content": "전시 소개 내용입니다.",
                                "location": {
                                  "placeName": "디스플레이유 갤러리",
                                  "latitude": 37.5665000,
                                  "longitude": 126.9780000
                                },
                                "qnaAccount": "@displayu",
                                "note": "입장 무료",
                                "organization": "디스플레이유",
                                "department": "디자인학과",
                                "displayType": "GRADUATION",
                                "displayFields": ["DESIGN", "VIDEO"],
                                "period": {
                                  "startDate": "2026-07-01",
                                  "endDate": "2026-07-10",
                                  "startTime": "10:00:00",
                                  "endTime": "18:00:00"
                                },
                                "artworkContentOpen": "IMMEDIATELY",
                                "exhibitionContentOpen": "ON_EXHIBITION",
                                "status": "PUBLISHED",
                                "invitationToken": "token-value",
                                "invitationDisabledAt": null,
                                "images": [
                                  {
                                    "imageId": 1,
                                    "imageUrl": "https://cdn.displayu.com/display/main.jpg",
                                    "imageType": "MAIN",
                                    "width": 1200,
                                    "height": 800,
                                    "sortOrder": 0
                                  },
                                  {
                                    "imageId": 2,
                                    "imageUrl": "https://cdn.displayu.com/display/detail.jpg",
                                    "imageType": "DETAIL",
                                    "width": 1200,
                                    "height": 800,
                                    "sortOrder": 1
                                  }
                                ],
                                "contentCategories": [
                                  {
                                    "categoryId": 1,
                                    "name": "전시 소개",
                                    "description": "전시의 주요 소개 이미지입니다.",
                                    "sortOrder": 0,
                                    "contents": [
                                      {
                                        "contentId": 1,
                                        "imageUrl": "https://cdn.displayu.com/display/content-1.jpg",
                                        "width": 1200,
                                        "height": 800,
                                        "sortOrder": 0
                                      }
                                    ]
                                  }
                                ],
                                "teamMembers": [
                                  {
                                    "teamMemberId": 1,
                                    "userId": 10,
                                    "displayNickname": "도현",
                                    "role": "TEAM_LEADER",
                                    "accepted": true
                                  },
                                  {
                                    "teamMemberId": 2,
                                    "userId": 11,
                                    "displayNickname": "민지",
                                    "role": "TEAM_MEM",
                                    "accepted": false
                                  }
                                ],
                                "invitations": [
                                  {
                                    "invitationId": 1,
                                    "inviterUserId": 10,
                                    "inviteeUserId": 11,
                                    "createdAt": "2026-06-20T12:00:00"
                                  }
                                ]
                              }
                            },
                            "error": null,
                            "meta": {
                              "timestamp": "2026-07-08T23:40:00",
                              "path": "/api/v1/display/1"
                            }
                          }
                          """)))
  public ApiResponseBody<DisplayDetailResponse> getDisplayDetail(
      @Parameter(description = "전시 ID", example = "1") @PathVariable Long displayId,
      HttpServletRequest request) {
    DisplayDetailResult result = getDisplayDetailService.getDisplayDetail(displayId);
    return ApiResponseBody.success(mapper.toResponse(result), request);
  }
}
