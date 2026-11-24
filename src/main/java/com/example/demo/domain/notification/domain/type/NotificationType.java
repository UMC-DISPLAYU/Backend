package com.example.demo.domain.notification.domain.type;

/** 알림함에 쌓이는 알림 종류. 값은 FE와 공유하는 API 응답 값이라 이름을 바꾸지 않는다. */
public enum NotificationType {
  REQUEST_APPROVED,
  REQUEST_REJECTED,
  EXHIBITION_INVITED,
  WORK_COAUTHOR_ADDED,
  WORK_PROXY_ASSIGNED,
  WORK_DELETED,
  WORK_DELETED_BY_ARTIST_LEAVE,
  EXHIBITION_MEMBER_REMOVED,
  EXHIBITION_DELETED,
  ARTIST_NEW_EXHIBITION(true),
  EXHIBITION_STARTED(true),
  OPERATOR_NOTICE;

  private final boolean oncePerTarget;

  NotificationType() {
    this(false);
  }

  NotificationType(boolean oncePerTarget) {
    this.oncePerTarget = oncePerTarget;
  }

  /**
   * 같은 대상(전시)에 대해 한 사람에게 한 번만 보내는 종류인지. 전시를 비공개 후 다시 공개하거나 예약 작업이 다시 돌아도 이미 받은 사람에게는 다시 만들지 않는다.
   */
  public boolean isOncePerTarget() {
    return oncePerTarget;
  }
}
