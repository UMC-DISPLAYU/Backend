package com.example.demo.domain.personalartwork.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

/**
 * 작가 인증 여부만 확인하기 위한 읽기 전용 참조 엔티티.
 *
 * <p>user 도메인의 User 애그리거트를 직접 가져다 쓰면 도메인 경계가 무너지므로, 필요한 컬럼만 최소한으로 매핑한다.
 */
@Getter
@Entity
@Table(name = "User")
public class PersonalArtworkUserVerificationJpaEntity {

  // 읽기 전용이지만, 같은 테이블을 매핑하는 User 애그리거트와 스키마 정의가 어긋나지 않도록 생성 전략을 맞춘다.
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "userId")
  private Long userId;

  @Column(name = "isVerified")
  private boolean verified;

  protected PersonalArtworkUserVerificationJpaEntity() {}
}
