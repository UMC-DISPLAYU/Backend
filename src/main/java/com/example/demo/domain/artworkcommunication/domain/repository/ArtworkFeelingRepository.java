package com.example.demo.domain.artworkcommunication.domain.repository;

import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkFeeling;
import java.util.Optional;

public interface ArtworkFeelingRepository {
  ArtworkFeeling save(ArtworkFeeling artworkFeeling);

  Optional<ArtworkFeeling> findById(Long feelingId);
}
