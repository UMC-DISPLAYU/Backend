package com.example.demo.domain.artworkcommunication.domain.repository;

import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkQuestion;
import java.util.Optional;

public interface ArtworkQuestionRepository {
  ArtworkQuestion save(ArtworkQuestion artworkQuestion);

  Optional<ArtworkQuestion> findById(Long artQueId);
}
