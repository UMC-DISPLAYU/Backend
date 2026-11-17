package com.example.demo.domain.notification.application.command;

import com.example.demo.domain.notification.application.event.NotificationRequestedEvent;
import com.example.demo.domain.notification.application.message.NotificationMessage;
import com.example.demo.domain.notification.application.usecase.RequestNotificationUseCase;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class RequestNotificationService implements RequestNotificationUseCase {

  private final ApplicationEventPublisher eventPublisher;

  public RequestNotificationService(ApplicationEventPublisher eventPublisher) {
    this.eventPublisher = eventPublisher;
  }

  @Override
  public void request(
      Collection<Long> recipientUserIds, Long actorUserId, NotificationMessage message) {
    Objects.requireNonNull(recipientUserIds, "recipientUserIds must not be null.");
    Objects.requireNonNull(message, "message must not be null.");

    List<Long> recipients =
        recipientUserIds.stream()
            .filter(Objects::nonNull)
            .filter(userId -> !userId.equals(actorUserId))
            .distinct()
            .toList();
    if (recipients.isEmpty()) {
      return;
    }
    eventPublisher.publishEvent(new NotificationRequestedEvent(recipients, message));
  }
}
