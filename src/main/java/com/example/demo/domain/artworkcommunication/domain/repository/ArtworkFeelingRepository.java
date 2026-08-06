package com.example.demo.domain.artworkcommunication.domain.repository;

import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkFeeling;
import java.util.List;
import java.util.Optional;

public interface ArtworkFeelingRepository {
  ArtworkFeeling save(ArtworkFeeling artworkFeeling);

  Optional<ArtworkFeeling> findById(Long feelingId);

  List<ArtworkFeeling> findActiveByDisplayArtworkId(Long displayArtworkId);

  List<ArtworkFeeling> findByDisplayArtworkIdWithCursorIncludingDeleted(
      Long displayArtworkId, Long cursorId, int limit);
}
