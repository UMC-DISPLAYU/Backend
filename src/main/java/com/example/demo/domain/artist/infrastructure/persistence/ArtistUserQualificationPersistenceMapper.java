package com.example.demo.domain.artist.infrastructure.persistence;

import com.example.demo.domain.artist.domain.model.ArtistUserQualification;
import org.springframework.stereotype.Component;

@Component
public class ArtistUserQualificationPersistenceMapper {

  public ArtistUserQualification toDomain(ArtistUserQualificationJpaEntity entity) {
    return ArtistUserQualification.restore(
        entity.getUserId(),
        entity.getSchoolEmail(),
        entity.getUniversityName(),
        entity.isSchoolVerified(),
        entity.isWithdrawn(),
        entity.getSourceRevision(),
        entity.getLastEventId(),
        entity.getUpdatedAt());
  }

  public ArtistUserQualificationJpaEntity toEntity(ArtistUserQualification qualification) {
    return new ArtistUserQualificationJpaEntity(
        qualification.getUserId(),
        qualification.getSchoolEmail(),
        qualification.getUniversityName(),
        qualification.isSchoolVerified(),
        qualification.isWithdrawn(),
        qualification.getSourceRevision(),
        qualification.getLastEventId(),
        qualification.getUpdatedAt());
  }
}
