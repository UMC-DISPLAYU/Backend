package com.example.demo.domain.notification.domain.aggregate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;

/**
 * 사용자가 알림함을 마지막으로 연 시각. 알림함을 열면 모든 알림이 한꺼번에 읽음이 되고 개별 읽음 처리가 없어서, 알림마다 읽은 시각을 두지 않고 사용자당 한 행만 둔다.
 */
@Getter
@Entity
@Table(name = "NotificationReadState")
public class NotificationReadState {

  @Id
  @Column(name = "userId")
  private Long userId;

  @Column(nullable = false)
  private LocalDateTime lastReadAt;

  protected NotificationReadState() {}
}
