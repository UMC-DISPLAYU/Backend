package com.example.demo.domain.display.infrastructure.persistence.adapter;

import com.example.demo.domain.display.application.port.ArtworkCreatorRenamePort;
import com.example.demo.domain.displayartwork.domain.repository.CreatorRepository;
import org.springframework.stereotype.Repository;

@Repository
public class JpaArtworkCreatorRenameAdapter implements ArtworkCreatorRenamePort {

  private final CreatorRepository creatorRepository;

  public JpaArtworkCreatorRenameAdapter(CreatorRepository creatorRepository) {
    this.creatorRepository = creatorRepository;
  }

  @Override
  public int rename(Long displayId, Long userId, String previousName, String newName) {
    return creatorRepository.renameCreatorNamesInDisplay(displayId, userId, previousName, newName);
  }
}
