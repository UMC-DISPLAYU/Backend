package com.example.demo.domain.artist.infrastructure.persistence.adapter;

import com.example.demo.domain.artist.domain.model.ArtistUserQualification;
import com.example.demo.domain.artist.domain.repository.ArtistUserQualificationRepository;
import com.example.demo.domain.artist.infrastructure.persistence.ArtistUserQualificationJpaRepository;
import com.example.demo.domain.artist.infrastructure.persistence.ArtistUserQualificationPersistenceMapper;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaArtistUserQualificationRepositoryAdapter
    implements ArtistUserQualificationRepository {

  private final ArtistUserQualificationJpaRepository jpaRepository;
  private final ArtistUserQualificationPersistenceMapper mapper;

  @Override
  public Optional<ArtistUserQualification> findByUserId(Long userId) {
    return jpaRepository.findById(userId).map(mapper::toDomain);
  }

  @Override
  public ArtistUserQualification save(ArtistUserQualification qualification) {
    return jpaRepository
        .findById(qualification.getUserId())
        .map(
            entity -> {
              entity.updateFrom(qualification);
              return mapper.toDomain(jpaRepository.save(entity));
            })
        .orElseGet(() -> mapper.toDomain(jpaRepository.save(mapper.toEntity(qualification))));
  }
}
