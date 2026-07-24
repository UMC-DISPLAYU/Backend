package com.example.demo.domain.displaycommunication.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
@Entity
@Table(name = "`User`")
public class DisplayReviewUserReferenceJpaEntity {
  @Id
  @Column(name = "userId")
  private Long userId;

  @Column(name = "deletedAt")
  private LocalDateTime deletedAt;

  @Column(name = "nickname")
  private String nickname;

  protected DisplayReviewUserReferenceJpaEntity() {}
}
