package com.example.demo.domain.notification.presentation;

import com.example.demo.domain.notification.application.command.ReadNotificationsService;
import com.example.demo.domain.notification.application.query.GetNotificationsService;
import com.example.demo.domain.notification.application.query.GetUnreadNotificationService;
import com.example.demo.domain.notification.presentation.docs.NotificationControllerDocs;
import com.example.demo.domain.notification.presentation.mapper.NotificationPresentationMapper;
import com.example.demo.domain.notification.presentation.response.NotificationCursorResponse;
import com.example.demo.domain.notification.presentation.response.NotificationUnreadResponse;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import com.example.demo.global.response.ApiResponseBody;
import com.example.demo.global.security.AuthUser;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/api/v1/notifications")
public class NotificationController implements NotificationControllerDocs {

  private final GetNotificationsService getNotificationsService;
  private final GetUnreadNotificationService getUnreadNotificationService;
  private final ReadNotificationsService readNotificationsService;
  private final NotificationPresentationMapper mapper;

  public NotificationController(
      GetNotificationsService getNotificationsService,
      GetUnreadNotificationService getUnreadNotificationService,
      ReadNotificationsService readNotificationsService,
      NotificationPresentationMapper mapper) {
    this.getNotificationsService = getNotificationsService;
    this.getUnreadNotificationService = getUnreadNotificationService;
    this.readNotificationsService = readNotificationsService;
    this.mapper = mapper;
  }

  @GetMapping
  @Override
  public ApiResponseBody<NotificationCursorResponse> getNotifications(
      @RequestParam(required = false) Long cursorId,
      @RequestParam(defaultValue = "10") int size,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest request) {
    return ApiResponseBody.success(
        mapper.toResponse(
            getNotificationsService.getNotifications(requireUserId(user), cursorId, size)),
        request);
  }

  @GetMapping("/unread")
  @Override
  public ApiResponseBody<NotificationUnreadResponse> getUnread(
      @AuthenticationPrincipal AuthUser user, HttpServletRequest request) {
    return ApiResponseBody.success(
        mapper.toResponse(getUnreadNotificationService.getUnread(requireUserId(user))), request);
  }

  @PostMapping("/read")
  @Override
  public ApiResponseBody<Void> readAll(
      @AuthenticationPrincipal AuthUser user, HttpServletRequest request) {
    readNotificationsService.readAll(requireUserId(user));
    return ApiResponseBody.success(null, request);
  }

  private Long requireUserId(AuthUser user) {
    if (user == null) {
      throw new BusinessException(GlobalErrorCode.UNAUTHORIZED);
    }
    return user.userId();
  }
}
