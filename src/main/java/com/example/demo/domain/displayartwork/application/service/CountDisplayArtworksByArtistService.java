package com.example.demo.domain.displayartwork.application.service;

import com.example.demo.domain.displayartwork.application.usecase.CountDisplayArtworksByArtistUseCase;
import com.example.demo.domain.displayartwork.domain.repository.DisplayArtworkRepository;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CountDisplayArtworksByArtistService implements CountDisplayArtworksByArtistUseCase {

  private final DisplayArtworkRepository displayArtworkRepository;

  public CountDisplayArtworksByArtistService(DisplayArtworkRepository displayArtworkRepository) {
    this.displayArtworkRepository = displayArtworkRepository;
  }

  @Override
  @Transactional(readOnly = true)
  public Map<Long, Integer> countByArtist(Long displayId) {
    return displayArtworkRepository.countActiveByArtistUserIdInDisplay(displayId);
  }
}
