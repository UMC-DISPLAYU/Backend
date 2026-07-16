package com.example.demo.domain.artworkcommunication.domain.repository;

import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkQuestion;
import java.util.List;
import java.util.Optional;

public interface ArtworkQuestionRepository {
  ArtworkQuestion save(ArtworkQuestion artworkQuestion);

  Optional<ArtworkQuestion> findById(Long artQueId);

  List<ArtworkQuestion> findActiveByDisplayArtworkIdWithCursor(
      Long displayArtworkId, Long cursorId, int limit);
}
