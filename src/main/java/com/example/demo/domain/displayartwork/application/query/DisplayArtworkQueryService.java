package com.example.demo.domain.displayartwork.application.query;

import com.example.demo.domain.displayartwork.application.result.DisplayArtworkResult;
import com.example.demo.domain.displayartwork.domain.error.DisplayArtworkErrorCode;
import com.example.demo.domain.displayartwork.domain.repository.DisplayArtworkRepository;
import com.example.demo.global.error.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DisplayArtworkQueryService {

  private final DisplayArtworkRepository displayArtworkRepository;

  public DisplayArtworkQueryService(DisplayArtworkRepository displayArtworkRepository) {
    this.displayArtworkRepository = displayArtworkRepository;
  }

  @Transactional(readOnly = true)
  public DisplayArtworkResult getDisplayArtworkDetail(Long displayArtworkId) {
    return displayArtworkRepository
        .findById(displayArtworkId)
        .filter(artwork -> !artwork.isDeleted())
        .map(DisplayArtworkResult::from)
        .orElseThrow(
            () -> new BusinessException(DisplayArtworkErrorCode.DISPLAY_ARTWORK_NOT_FOUND));
  }
}
