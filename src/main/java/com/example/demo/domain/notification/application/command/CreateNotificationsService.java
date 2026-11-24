package com.example.demo.domain.notification.application.command;

import com.example.demo.domain.notification.application.event.NotificationRequestedEvent;
import com.example.demo.domain.notification.application.message.NotificationMessage;
import com.example.demo.domain.notification.domain.aggregate.Notification;
import com.example.demo.domain.notification.domain.repository.NotificationRepository;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateNotificationsService {

  private final NotificationRepository notificationRepository;

  public CreateNotificationsService(NotificationRepository notificationRepository) {
    this.notificationRepository = notificationRepository;
  }

  /** 요청한 트랜잭션은 이미 끝난 뒤라 새 트랜잭션에서 저장한다. */
  @Transactional
  public void create(NotificationRequestedEvent event) {
    NotificationMessage message = event.message();
    List<Long> recipientUserIds = excludeAlreadyNotified(event.recipientUserIds(), message);
    if (recipientUserIds.isEmpty()) {
      return;
    }
    List<Notification> notifications =
        recipientUserIds.stream()
            .map(
                recipientUserId ->
                    Notification.create(
                        recipientUserId,
                        message.type(),
                        message.title(),
                        message.body(),
                        message.target()))
            .toList();
    notificationRepository.saveAll(notifications);
  }

  /** 대상마다 한 번만 보내는 종류면, 같은 대상으로 이미 받은 사람을 뺀다. 그래서 같은 요청이 여러 번 와도 결과가 같다. */
  private List<Long> excludeAlreadyNotified(
      List<Long> recipientUserIds, NotificationMessage message) {
    if (!message.type().isOncePerTarget() || message.target() == null) {
      return recipientUserIds;
    }
    Set<Long> alreadyNotified =
        notificationRepository.findRecipientsAlreadyNotified(
            message.type(), message.target().id(), recipientUserIds);
    return recipientUserIds.stream().filter(userId -> !alreadyNotified.contains(userId)).toList();
  }
}
