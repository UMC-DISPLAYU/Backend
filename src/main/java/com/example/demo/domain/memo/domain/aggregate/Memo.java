package com.example.demo.domain.memo.domain.aggregate;

import com.example.demo.global.entity.SoftDeleteBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.Objects;
import lombok.Getter;

@Getter
@Entity
@Table(name = "Memo")
public class Memo extends SoftDeleteBaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "memoId")
  private Long id;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String content;

  private LocalDate visitDate;

  private Long archiveDisplayId;

  private Long archiveWorkId;

  protected Memo() {}

  private Memo(String content, LocalDate visitDate, Long archiveDisplayId, Long archiveWorkId) {
    requireExactlyOneTarget(archiveDisplayId, archiveWorkId);
    this.archiveDisplayId = archiveDisplayId;
    this.archiveWorkId = archiveWorkId;
    changeContent(content, visitDate);
  }

  public static Memo createForDisplay(String content, LocalDate visitDate, Long archiveDisplayId) {
    return new Memo(
        content, visitDate, Objects.requireNonNull(archiveDisplayId, "archiveDisplayId must not be null."), null);
  }

  public static Memo createForWork(String content, LocalDate visitDate, Long archiveWorkId) {
    return new Memo(
        content, visitDate, null, Objects.requireNonNull(archiveWorkId, "archiveWorkId must not be null."));
  }

  public void changeContent(String content, LocalDate visitDate) {
    this.content = requireNonBlank(content, "content");
    this.visitDate = visitDate;
  }

  private static void requireExactlyOneTarget(Long archiveDisplayId, Long archiveWorkId) {
    boolean hasDisplay = archiveDisplayId != null;
    boolean hasWork = archiveWorkId != null;
    if (hasDisplay == hasWork) {
      throw new IllegalArgumentException(
          "archiveDisplayId와 archiveWorkId 중 정확히 하나만 값이 있어야 합니다.");
    }
  }

  private static String requireNonBlank(String value, String fieldName) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException(fieldName + " must not be blank.");
    }
    return value;
  }
}
