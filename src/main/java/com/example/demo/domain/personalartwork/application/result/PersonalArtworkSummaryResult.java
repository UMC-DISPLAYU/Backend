package com.example.demo.domain.personalartwork.application.result;

import com.example.demo.domain.personalartwork.domain.aggregate.PersonalArtwork;
import com.example.demo.domain.personalartwork.domain.entity.PersonalArtworkImage;
import java.time.LocalDateTime;
import java.util.Comparator;

public record PersonalArtworkSummaryResult(
    Long personalArtworkId,
    String artworkName,
    String thumbnailUrl,
    String type,
    LocalDateTime createdAt) {

  public static PersonalArtworkSummaryResult from(PersonalArtwork personalArtwork) {
    return new PersonalArtworkSummaryResult(
        personalArtwork.getId(),
        personalArtwork.getArtworkName(),
        findThumbnailUrl(personalArtwork),
        personalArtwork.getType().name(),
        personalArtwork.getCreatedAt());
  }

  private static String findThumbnailUrl(PersonalArtwork personalArtwork) {
    return personalArtwork.getImages().stream()
        .filter(image -> !image.isDeleted())
        .sorted(
            Comparator.comparing(PersonalArtworkImage::isThumbnail)
                .reversed()
                .thenComparing(PersonalArtworkImage::getSortOrder))
        .map(PersonalArtworkImage::getImageUrl)
        .findFirst()
        .orElse(null);
  }
}
