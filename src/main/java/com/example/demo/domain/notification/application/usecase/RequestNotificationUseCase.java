package com.example.demo.domain.notification.application.usecase;

import com.example.demo.domain.notification.application.message.NotificationMessage;
import java.util.Collection;

/** 다른 도메인이 알림을 요청할 때 쓰는 창구. 알림은 요청한 트랜잭션이 커밋된 뒤에 저장된다. */
public interface RequestNotificationUseCase {

  /**
   * 받는 사람들에게 같은 알림을 한 건씩 요청한다. 직접 한 사람(actorUserId)과 null은 제외하고, 같은 사람이 여러 번 들어 있어도 한 건만 만든다.
   *
   * @param actorUserId 이 알림을 일으킨 사람. 본인이 한 일에는 알림을 보내지 않는다. 시스템이 일으킨 경우 null
   */
  void request(Collection<Long> recipientUserIds, Long actorUserId, NotificationMessage message);
}
