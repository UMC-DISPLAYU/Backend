package com.example.demo.domain.notification.application.event;

import com.example.demo.domain.notification.application.command.CreateNotificationsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 알림은 실패해도 본 작업(작품 등록·삭제 등)은 그대로 성공해야 하므로, 요청한 트랜잭션이 커밋된 뒤 별도 스레드에서 저장한다. 본 작업이 롤백되면 알림도 만들지 않는다.
 * 재시도 후에도 실패하면 로그만 남긴다. 알림은 안내용이고 원본 정보는 각 화면에 남아 있어서 유실을 허용한다.
 */
@Component
public class NotificationRequestedEventHandler {

  private static final Logger log =
      LoggerFactory.getLogger(NotificationRequestedEventHandler.class);
  private static final int MAX_RETRY_COUNT = 3;

  private final CreateNotificationsService createNotificationsService;

  public NotificationRequestedEventHandler(CreateNotificationsService createNotificationsService) {
    this.createNotificationsService = createNotificationsService;
  }

  @Async("notificationExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handle(NotificationRequestedEvent event) {
    for (int attempt = 1; attempt <= MAX_RETRY_COUNT; attempt++) {
      try {
        createNotificationsService.create(event);
        return;
      } catch (RuntimeException e) {
        if (attempt == MAX_RETRY_COUNT) {
          log.error(
              "Failed to create notifications. type={} recipients={}",
              event.message().type(),
              event.recipientUserIds(),
              e);
        } else {
          log.warn(
              "Retrying notification creation. type={} attempt={}/{}",
              event.message().type(),
              attempt,
              MAX_RETRY_COUNT,
              e);
        }
      }
    }
  }
}
