package com.example.demo.domain.archive.domain.aggregate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "ArchiveArtist")
public class ArchiveArtist {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "archiveArtistId")
  private Long id;

  @Column(nullable = false)
  private Long artistProfileId;

  // 저장 시점에 조회해 둔, 저장 대상 작가 본인의 userId (참조 무결성 없음, 조회 편의용 비정규화 컬럼).
  // 취소(delete) 시 ArtistProfile을 다시 조회하지 않고 이 값으로 바로 찾기 위해 존재한다 —
  // 그래야 작가가 이후 프로필을 삭제해도 저장했던 사용자가 계속 취소할 수 있다.
  // 이 컬럼이 추가되기 전에 저장된 레코드는 null일 수 있다.
  @Column private Long artistUserId;

  @Column(nullable = false)
  private Long userId;

  @CreatedDate
  @Column(nullable = false, updatable = false)
  private LocalDateTime savedAt;

  protected ArchiveArtist() {}

  private ArchiveArtist(Long artistProfileId, Long artistUserId, Long userId) {
    this.artistProfileId =
        Objects.requireNonNull(artistProfileId, "artistProfileId must not be null.");
    this.artistUserId = Objects.requireNonNull(artistUserId, "artistUserId must not be null.");
    this.userId = Objects.requireNonNull(userId, "userId must not be null.");
  }

  public static ArchiveArtist create(Long artistProfileId, Long artistUserId, Long userId) {
    return new ArchiveArtist(artistProfileId, artistUserId, userId);
  }

  public boolean isOwnedBy(Long userId) {
    return Objects.equals(this.userId, userId);
  }
}
