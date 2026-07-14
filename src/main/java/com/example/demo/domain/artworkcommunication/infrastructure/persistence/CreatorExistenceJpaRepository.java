package com.example.demo.domain.artworkcommunication.infrastructure.persistence;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CreatorExistenceJpaRepository
    extends JpaRepository<CreatorReferenceJpaEntity, Long> {

  Optional<CreatorReferenceJpaEntity> findByDisplayArtworkIdAndUserId(
      Long displayArtworkId, Long userId);

  Optional<CreatorReferenceJpaEntity>
      findFirstByDisplayArtworkIdAndIsContactTrueOrderByCreatorIdAsc(Long displayArtworkId);
}
