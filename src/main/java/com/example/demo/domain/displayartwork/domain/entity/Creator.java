package com.example.demo.domain.displayartwork.domain.entity;

import com.example.demo.domain.displayartwork.domain.type.CreatorRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;
import lombok.Getter;

@Getter
@Entity
@Table(name = "Creator")
public class Creator {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "creatorId")
  private Long id;

  @Column(nullable = false)
  private String creatorName;

  @Column(nullable = false)
  private boolean isContact;

  @Column(nullable = false)
  private boolean isLeader;

  /** isLeader는 대표 작가 여부만 표현해서 공동 작업자와 QnA 담당 전용을 구분하지 못한다. 셋을 구분해야 하는 조회에서는 이 값을 사용한다. */
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private CreatorRole role;

  private Long userId;

  @Column(nullable = false)
  private Long displayArtworkId;

  protected Creator() {}

  public Creator(
      Long id,
      String creatorName,
      boolean isContact,
      CreatorRole role,
      Long userId,
      Long displayArtworkId) {
    this.id = id;
    this.creatorName = requireNonBlank(creatorName, "creatorName");
    this.isContact = isContact;
    this.role = Objects.requireNonNull(role, "role must not be null.");
    // 기존 조회(대표 작가 단건 조회 등)가 아직 isLeader를 사용하므로 role에서 파생해 함께 저장한다.
    this.isLeader = role == CreatorRole.LEAD_ARTIST;
    this.userId = userId;
    this.displayArtworkId = displayArtworkId;
  }

  public boolean isCoAuthor() {
    return role == CreatorRole.CO_AUTHOR;
  }

  private static String requireNonBlank(String value, String fieldName) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException(fieldName + " must not be blank.");
    }
    return value;
  }
}
