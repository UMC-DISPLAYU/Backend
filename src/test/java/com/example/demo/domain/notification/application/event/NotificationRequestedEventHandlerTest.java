package com.example.demo.domain.notification.application.event;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.demo.domain.notification.application.command.CreateNotificationsService;
import com.example.demo.domain.notification.application.message.NotificationMessages;
import java.util.List;
import org.junit.jupiter.api.Test;

class NotificationRequestedEventHandlerTest {

  private final CreateNotificationsService createNotificationsService =
      mock(CreateNotificationsService.class);
  private final NotificationRequestedEventHandler handler =
      new NotificationRequestedEventHandler(createNotificationsService);
  private final NotificationRequestedEvent event =
      new NotificationRequestedEvent(List.of(2L), NotificationMessages.coAuthorAdded("작품", 7L));

  @Test
  void handleRetriesUntilCreationSucceeds() {
    doThrow(new RuntimeException("일시 장애"))
        .doNothing()
        .when(createNotificationsService)
        .create(event);

    handler.handle(event);

    verify(createNotificationsService, times(2)).create(event);
  }

  @Test
  void handleGivesUpAfterThreeFailuresWithoutThrowing() {
    doThrow(new RuntimeException("계속 실패")).when(createNotificationsService).create(event);

    assertThatCode(() -> handler.handle(event)).doesNotThrowAnyException();

    verify(createNotificationsService, times(3)).create(event);
  }

  @Test
  void handleCreatesOnceWhenFirstAttemptSucceeds() {
    doNothing().when(createNotificationsService).create(event);

    handler.handle(event);

    verify(createNotificationsService, times(1)).create(event);
  }
}
