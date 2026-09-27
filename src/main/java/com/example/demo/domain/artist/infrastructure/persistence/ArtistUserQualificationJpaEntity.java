package com.example.demo.domain.artist.infrastructure.persistence;

import com.example.demo.domain.artist.domain.model.ArtistUserQualification;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Entity
@Table(name = "ArtistUserQualification")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArtistUserQualificationJpaEntity {

  @Id
  @Column(name = "userId")
  private Long userId;

  @Column(name = "schoolEmail")
  private String schoolEmail;

  @Column(name = "universityName")
  private String universityName;

  @Column(name = "schoolVerified", nullable = false)
  private boolean schoolVerified;

  @Column(name = "withdrawn", nullable = false)
  private boolean withdrawn;

  @Column(name = "sourceRevision", nullable = false)
  private long sourceRevision;

  @Column(name = "lastEventId", length = 36)
  @JdbcTypeCode(SqlTypes.VARCHAR)
  private UUID lastEventId;

  @Column(name = "updatedAt", nullable = false)
  private Instant updatedAt;

  @Version
  @Column(name = "persistenceVersion", nullable = false)
  private long persistenceVersion;

  public ArtistUserQualificationJpaEntity(
      Long userId,
      String schoolEmail,
      String universityName,
      boolean schoolVerified,
      boolean withdrawn,
      long sourceRevision,
      UUID lastEventId,
      Instant updatedAt) {
    this.userId = userId;
    this.schoolEmail = schoolEmail;
    this.universityName = universityName;
    this.schoolVerified = schoolVerified;
    this.withdrawn = withdrawn;
    this.sourceRevision = sourceRevision;
    this.lastEventId = lastEventId;
    this.updatedAt = updatedAt;
  }

  public void updateFrom(ArtistUserQualification qualification) {
    this.schoolEmail = qualification.getSchoolEmail();
    this.universityName = qualification.getUniversityName();
    this.schoolVerified = qualification.isSchoolVerified();
    this.withdrawn = qualification.isWithdrawn();
    this.sourceRevision = qualification.getSourceRevision();
    this.lastEventId = qualification.getLastEventId();
    this.updatedAt = qualification.getUpdatedAt();
  }
}
