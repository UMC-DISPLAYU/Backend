package com.example.demo.domain.artworkcommunication.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataCreatorExistenceJpaRepository
    extends JpaRepository<CreatorReferenceJpaEntity, Long> {

  Optional<CreatorReferenceJpaEntity> findByDisplayArtworkIdAndUserId(
      Long displayArtworkId, Long userId);

  Optional<CreatorReferenceJpaEntity>
      findFirstByDisplayArtworkIdAndIsContactTrueOrderByCreatorIdAsc(Long displayArtworkId);

  List<CreatorReferenceJpaEntity> findByDisplayArtworkIdAndUserIdIn(
      Long displayArtworkId, Set<Long> userIds);

  List<CreatorReferenceJpaEntity> findByCreatorIdIn(Set<Long> creatorIds);
}
