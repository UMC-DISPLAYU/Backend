package com.example.demo.domain.artworkcommunication.domain.repository;

import java.util.Optional;

public interface CreatorExistenceRepository {
  Optional<String> findCreatorNameByDisplayArtworkIdAndUserId(Long displayArtworkId, Long userId);
}
