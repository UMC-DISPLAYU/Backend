package com.example.demo.domain.displayartwork.domain.repository;

import com.example.demo.domain.displayartwork.domain.aggregate.DisplayArtwork;
import java.util.Optional;

public interface DisplayArtworkRepository {

  Optional<DisplayArtwork> findById(Long displayArtworkId);

  int countByDisplayId(Long displayId);

  DisplayArtwork save(DisplayArtwork displayArtwork);
}
