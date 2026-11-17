package com.example.demo.domain.notification.application.command;

import com.example.demo.domain.notification.application.event.NotificationRequestedEvent;
import com.example.demo.domain.notification.application.message.NotificationMessage;
import com.example.demo.domain.notification.domain.aggregate.Notification;
import com.example.demo.domain.notification.domain.repository.NotificationRepository;
import java.util.List;
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
    List<Notification> notifications =
        event.recipientUserIds().stream()
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
}
