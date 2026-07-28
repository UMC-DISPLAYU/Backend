package com.example.demo.domain.artworkcommunication.domain.repository;

import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkQuestion;
import java.util.List;
import java.util.Optional;

public interface ArtworkQuestionRepository {
  ArtworkQuestion save(ArtworkQuestion artworkQuestion);

  Optional<ArtworkQuestion> findById(Long questionId);

  List<ArtworkQuestion> findActiveByDisplayArtworkIdWithCursor(
      Long displayArtworkId, Long cursorId, int limit);
}
