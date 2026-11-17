package com.example.demo.domain.notification.application.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.example.demo.domain.notification.application.event.NotificationRequestedEvent;
import com.example.demo.domain.notification.application.message.NotificationMessage;
import com.example.demo.domain.notification.application.message.NotificationMessages;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;

class RequestNotificationServiceTest {

  private final ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
  private final RequestNotificationService service = new RequestNotificationService(eventPublisher);
  private final NotificationMessage message = NotificationMessages.coAuthorAdded("작품", 7L);

  @Test
  void requestExcludesActorNullAndDuplicatedRecipients() {
    service.request(Arrays.asList(2L, 1L, null, 2L, 3L), 1L, message);

    ArgumentCaptor<NotificationRequestedEvent> captor =
        ArgumentCaptor.forClass(NotificationRequestedEvent.class);
    verify(eventPublisher).publishEvent(captor.capture());
    assertThat(captor.getValue().recipientUserIds()).containsExactly(2L, 3L);
    assertThat(captor.getValue().message()).isEqualTo(message);
  }

  @Test
  void requestKeepsAllRecipientsWhenSystemIsActor() {
    service.request(List.of(1L, 2L), null, message);

    ArgumentCaptor<NotificationRequestedEvent> captor =
        ArgumentCaptor.forClass(NotificationRequestedEvent.class);
    verify(eventPublisher).publishEvent(captor.capture());
    assertThat(captor.getValue().recipientUserIds()).containsExactly(1L, 2L);
  }

  @Test
  void requestPublishesNothingWhenOnlyActorRemains() {
    service.request(List.of(1L), 1L, message);
    service.request(List.of(), 1L, message);

    verify(eventPublisher, never()).publishEvent(any(Object.class));
  }
}
