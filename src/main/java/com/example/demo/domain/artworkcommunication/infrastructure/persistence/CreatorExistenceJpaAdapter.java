package com.example.demo.domain.artworkcommunication.infrastructure.persistence;

import com.example.demo.domain.artworkcommunication.domain.repository.CreatorExistenceRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.CreatorExistenceRepository.ContactCreator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CreatorExistenceJpaAdapter implements CreatorExistenceRepository {

  private final CreatorExistenceJpaRepository repository;

  @Override
  public Optional<String> findParticipantNameByDisplayArtworkIdAndUserId(
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

  @Override
  public Optional<ContactCreator> findContactCreatorByDisplayArtworkId(Long displayArtworkId) {
    return repository
        .findFirstByDisplayArtworkIdAndIsContactTrueOrderByCreatorIdAsc(displayArtworkId)
        .map(creator -> new ContactCreator(creator.getCreatorId(), creator.getCreatorName()));
  }

  @Override
  public Map<Long, String> findCreatorNamesByDisplayArtworkIdAndUserIds(
      Long displayArtworkId, Set<Long> userIds) {
    if (userIds.isEmpty()) {
      return Map.of();
    }

    return repository.findByDisplayArtworkIdAndUserIdIn(displayArtworkId, userIds).stream()
        .collect(
            Collectors.toMap(
                CreatorReferenceJpaEntity::getUserId,
                CreatorReferenceJpaEntity::getCreatorName,
                (first, ignored) -> first));
  }

  @Override
  public Map<Long, String> findCreatorNamesByIds(Set<Long> creatorIds) {
    if (creatorIds.isEmpty()) {
      return Map.of();
    }

    return repository.findByCreatorIdIn(creatorIds).stream()
        .collect(
            Collectors.toMap(
                CreatorReferenceJpaEntity::getCreatorId,
                CreatorReferenceJpaEntity::getCreatorName,
                (first, ignored) -> first));
  }
}
