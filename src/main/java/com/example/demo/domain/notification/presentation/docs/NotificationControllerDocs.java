package com.example.demo.domain.notification.presentation.docs;

import com.example.demo.domain.notification.presentation.response.NotificationCursorResponse;
import com.example.demo.domain.notification.presentation.response.NotificationUnreadResponse;
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
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Notification", description = "알림함 API")
@SecurityRequirement(name = "Authorization")
public interface NotificationControllerDocs {

  String UNAUTHORIZED_EXAMPLE =
      """
      {
        "resultType": "FAIL",
        "success": null,
        "error": { "code": "UNAUTHORIZED", "message": "인증이 필요합니다.", "details": null },
        "meta": { "timestamp": "2026-11-16T09:05:00", "path": "/api/v1/notifications" }
      }
      """;

  @Operation(
      summary = "알림 목록 조회",
      description =
          """
          내 알림을 최신순으로 커서 기반 페이지네이션 조회합니다.
          - isRead는 알림함을 열기 전 기준입니다. 목록을 받은 뒤 POST /api/v1/notifications/read 로 읽음 처리해 주세요.
          - body는 조각 배열입니다. 순서대로 이어 붙이고 bold가 true인 조각만 굵게 표시합니다.
          - target은 누르면 이동하는 알림에만 값이 있고, 나머지는 null입니다.
          """)
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
                                "notifications": [
                                  {
                                    "notificationId": 31,
                                    "type": "ARTIST_NEW_EXHIBITION",
                                    "title": "저장한 작가의 새 전시",
                                    "body": [
                                      { "text": "송하윤", "bold": true },
                                      { "text": "님이 참여한 “", "bold": false },
                                      { "text": "재생 중", "bold": true },
                                      { "text": "” 전시가 공개됐어요. 12.02 - 12.07 · 미온미디어관 B1", "bold": false }
                                    ],
                                    "isRead": false,
                                    "createdAt": "2026-12-02T09:00:00",
                                    "target": { "type": "DISPLAY_DETAIL", "id": 7 }
                                  },
                                  {
                                    "notificationId": 30,
                                    "type": "EXHIBITION_MEMBER_REMOVED",
                                    "title": "전시에서 내보내짐",
                                    "body": [
                                      { "text": "“", "bold": false },
                                      { "text": "손끝의 지도", "bold": true },
                                      { "text": "” 전시에서 내보내졌어요. 작가로 등록돼 있던 작품 ", "bold": false },
                                      { "text": "2점", "bold": true },
                                      { "text": "이 함께 삭제됐어요.", "bold": false }
                                    ],
                                    "isRead": true,
                                    "createdAt": "2026-11-28T14:20:00",
                                    "target": null
                                  }
                                ],
                                "nextCursorId": 30,
                                "size": 2,
                                "hasNext": true
                              }
                            },
                            "error": null,
                            "meta": { "timestamp": "2026-12-02T09:05:00", "path": "/api/v1/notifications" }
                          }
                          """)))
  @ApiResponse(
      responseCode = "401",
      content = @Content(examples = @ExampleObject(value = UNAUTHORIZED_EXAMPLE)))
  ApiResponseBody<NotificationCursorResponse> getNotifications(
      @Parameter(description = "이전 응답의 nextCursorId. 첫 요청이면 전달하지 않음")
          @RequestParam(required = false)
          @Positive Long cursorId,
      @Parameter(description = "한 번에 불러올 개수") @RequestParam(defaultValue = "10") @Min(1) @Max(50) int size,
      AuthUser user,
      HttpServletRequest request);

  @Operation(
      summary = "안 읽은 알림 여부 조회",
      description = "알림함을 마지막으로 연 뒤 새 알림이 있는지 조회합니다. 마이 알림함 아이콘의 빨간 점 표시에 사용합니다.")
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
                            "success": { "data": { "hasUnread": true } },
                            "error": null,
                            "meta": { "timestamp": "2026-12-02T09:05:00", "path": "/api/v1/notifications/unread" }
                          }
                          """)))
  @ApiResponse(
      responseCode = "401",
      content = @Content(examples = @ExampleObject(value = UNAUTHORIZED_EXAMPLE)))
  ApiResponseBody<NotificationUnreadResponse> getUnread(AuthUser user, HttpServletRequest request);

  @Operation(
      summary = "알림함 열기 (전체 읽음 처리)",
      description = "알림함을 연 시각까지 생긴 알림을 모두 읽음 처리합니다. 알림 목록을 받은 뒤 호출해 주세요. 개별 읽음 처리는 없습니다.")
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
                            "success": { "data": null },
                            "error": null,
                            "meta": { "timestamp": "2026-12-02T09:05:00", "path": "/api/v1/notifications/read" }
                          }
                          """)))
  @ApiResponse(
      responseCode = "401",
      content = @Content(examples = @ExampleObject(value = UNAUTHORIZED_EXAMPLE)))
  ApiResponseBody<Void> readAll(AuthUser user, HttpServletRequest request);
}
