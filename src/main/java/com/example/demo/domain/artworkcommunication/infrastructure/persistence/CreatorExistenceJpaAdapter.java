package com.example.demo.domain.artworkcommunication.infrastructure.persistence;

import com.example.demo.domain.artworkcommunication.domain.repository.CreatorExistenceRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.CreatorExistenceRepository.ContactCreator;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CreatorExistenceJpaAdapter implements CreatorExistenceRepository {

  private final CreatorExistenceJpaRepository repository;

  @Override
  public Optional<String> findCreatorNameByDisplayArtworkIdAndUserId(
      Long displayArtworkId, Long userId) {
    return repository
        .findByDisplayArtworkIdAndUserId(displayArtworkId, userId)
        .map(CreatorReferenceJpaEntity::getCreatorName);
  }

  @Override
  public Optional<ContactCreator> findContactCreatorByDisplayArtworkIdAndUserId(
      Long displayArtworkId, Long userId) {
    return repository
        .findByDisplayArtworkIdAndUserId(displayArtworkId, userId)
        .filter(creator -> Boolean.TRUE.equals(creator.getIsContact()))
        .map(creator -> new ContactCreator(creator.getCreatorId(), creator.getCreatorName()));
  }
}
