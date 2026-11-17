package com.example.demo.domain.notification;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.domain.notification.application.message.NotificationMessages;
import com.example.demo.domain.notification.application.usecase.RequestNotificationUseCase;
import com.example.demo.domain.notification.domain.aggregate.Notification;
import com.example.demo.domain.notification.domain.type.NotificationType;
import com.example.demo.domain.notification.infrastructure.persistence.SpringDataNotificationJpaRepository;
import java.util.List;
import java.util.function.Supplier;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 알림 요청이 커밋 후 별도 스레드에서 실제로 저장되는지 확인한다. 리스너는 커밋이 돼야 동작하므로 @Transactional 롤백 테스트를 쓰지 않고 직접 커밋한 뒤 저장된
 * 알림을 지운다. 설정은 DemoApplicationTests와 같게 두어 스프링 컨텍스트를 새로 만들지 않는다.
 */
@SpringBootTest
@ActiveProfiles("test")
class NotificationRequestIntegrationTest {

  private static final Long ACTOR = 910_001L;
  private static final Long RECIPIENT = 910_002L;
  private static final Long OTHER_RECIPIENT = 910_003L;

  @Autowired private RequestNotificationUseCase requestNotificationUseCase;
  @Autowired private SpringDataNotificationJpaRepository notificationJpaRepository;
  @Autowired private TransactionTemplate transactionTemplate;

  @AfterEach
  void cleanUp() {
    notificationJpaRepository.deleteAll(
        notificationJpaRepository.findAll().stream()
            .filter(
                notification ->
                    List.of(ACTOR, RECIPIENT, OTHER_RECIPIENT)
                        .contains(notification.getRecipientUserId()))
            .toList());
  }

  @Test
  void requestedNotificationIsSavedAfterCommitForRecipientsExceptActor() throws Exception {
    transactionTemplate.executeWithoutResult(
        status ->
            requestNotificationUseCase.request(
                List.of(ACTOR, RECIPIENT, OTHER_RECIPIENT),
                ACTOR,
                NotificationMessages.coAuthorAdded("얇은 경계", 7L)));

    List<Notification> recipientNotifications = waitUntilNotEmpty(() -> notificationsOf(RECIPIENT));
    List<Notification> otherRecipientNotifications =
        waitUntilNotEmpty(() -> notificationsOf(OTHER_RECIPIENT));

    assertThat(recipientNotifications)
        .singleElement()
        .extracting(Notification::getType)
        .isEqualTo(NotificationType.WORK_COAUTHOR_ADDED);
    assertThat(otherRecipientNotifications).hasSize(1);
    assertThat(notificationsOf(ACTOR)).isEmpty();
  }

  @Test
  void requestedNotificationIsNotSavedWhenTransactionRollsBack() throws Exception {
    transactionTemplate.executeWithoutResult(
        status -> {
          requestNotificationUseCase.request(
              List.of(RECIPIENT), ACTOR, NotificationMessages.coAuthorAdded("얇은 경계", 7L));
          status.setRollbackOnly();
        });

    Thread.sleep(1_000);

    assertThat(notificationsOf(RECIPIENT)).isEmpty();
  }

  private List<Notification> notificationsOf(Long recipientUserId) {
    return notificationJpaRepository.findByRecipientBeforeCursorOrderByIdDesc(
        recipientUserId, null, PageRequest.of(0, 10));
  }

  private static List<Notification> waitUntilNotEmpty(Supplier<List<Notification>> query)
      throws InterruptedException {
    for (int i = 0; i < 50; i++) {
      List<Notification> result = query.get();
      if (!result.isEmpty()) {
        return result;
      }
      Thread.sleep(100);
    }
    return query.get();
  }
}
