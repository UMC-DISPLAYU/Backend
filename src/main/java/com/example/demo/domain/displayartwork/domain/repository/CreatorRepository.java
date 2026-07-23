package com.example.demo.domain.displayartwork.domain.repository;

import com.example.demo.domain.displayartwork.domain.entity.Creator;
import java.util.List;
import java.util.Optional;

public interface CreatorRepository {

  List<Creator> findByDisplayArtworkId(Long displayArtworkId);

  Optional<Creator> findLeaderByDisplayArtworkId(Long displayArtworkId);

  List<Creator> findLeadersByDisplayArtworkIds(List<Long> displayArtworkIds);

  Creator save(Creator creator);

  List<Creator> saveAll(List<Creator> creators);

  void deleteAllByDisplayArtworkId(Long displayArtworkId);
}
