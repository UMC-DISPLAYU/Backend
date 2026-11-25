package com.example.demo.domain.displayartwork.domain.vo;

import com.example.demo.domain.displayartwork.domain.error.DisplayArtworkErrorCode;
import com.example.demo.global.error.BusinessException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record ArtworkPart(
    @Column(nullable = false) String artworkName,
    Integer productionYear,
    String materialMedia,
    String size) {
  public ArtworkPart {
    if (artworkName == null || artworkName.isBlank()) {
      throw new BusinessException(DisplayArtworkErrorCode.ARTWORK_PART_NAME_REQUIRED);
    }
  }
}
