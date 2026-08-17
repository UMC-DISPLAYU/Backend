package com.example.demo.domain.personalartwork.application.result;

import com.example.demo.domain.personalartwork.domain.aggregate.PersonalArtwork;
import com.example.demo.domain.personalartwork.domain.entity.PersonalArtworkImage;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

public record PersonalArtworkSummaryResult(
    Long personalArtworkId,
    String artworkName,
    String artistName,
    String thumbnailUrl,
    String type,
    List<String> types,
    LocalDateTime createdAt) {

  /** 작가명은 작품이 아니라 작가 프로필에 있으므로 밖에서 조회해 넘겨받는다. 프로필이 없으면 null이다. */
  public static PersonalArtworkSummaryResult from(
      PersonalArtwork personalArtwork, String artistName) {
    return new PersonalArtworkSummaryResult(
        personalArtwork.getId(),
        personalArtwork.getArtworkName(),
        artistName,
        findThumbnailUrl(personalArtwork),
        personalArtwork.getType().name(),
        personalArtwork.getFieldTypes().stream().map(Enum::name).toList(),
        personalArtwork.getCreatedAt());
  }

  private static String findThumbnailUrl(PersonalArtwork personalArtwork) {
    return personalArtwork.getImages().stream()
        .sorted(Comparator.comparing(PersonalArtworkImage::isThumbnail).reversed())
        .map(PersonalArtworkImage::getImageUrl)
        .findFirst()
        .orElse(null);
  }
}
