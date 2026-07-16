package com.example.demo.domain.artworkcommunication.domain.repository;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface CreatorExistenceRepository {
  Optional<String> findCreatorNameByDisplayArtworkIdAndUserId(Long displayArtworkId, Long userId);

  Optional<ContactCreator> findContactCreatorByDisplayArtworkIdAndUserId(
      Long displayArtworkId, Long userId);

  Optional<ContactCreator> findContactCreatorByDisplayArtworkId(Long displayArtworkId);

  Map<Long, String> findCreatorNamesByDisplayArtworkIdAndUserIds(
      Long displayArtworkId, Set<Long> userIds);

  Map<Long, String> findCreatorNamesByIds(Set<Long> creatorIds);

  record ContactCreator(Long creatorId, String creatorName) {}
}
