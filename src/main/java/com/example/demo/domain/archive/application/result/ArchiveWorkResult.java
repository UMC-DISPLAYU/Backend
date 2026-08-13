package com.example.demo.domain.archive.application.result;

import com.example.demo.domain.archive.domain.aggregate.ArchivePersonalWork;
import com.example.demo.domain.archive.domain.aggregate.ArchiveWork;
import com.example.demo.domain.displayartwork.application.result.ArtworkSummaryResult;
import com.example.demo.domain.personalartwork.application.result.PersonalArtworkSummaryResult;
import java.time.LocalDateTime;

public record ArchiveWorkResult(
    String artworkImageUrl,
    String artworkName,
    String artistName,
    String memo,
    Long archiveWorkId,
    Long displayArtworkId,
    Long personalArtworkId,
    Long userId,
    LocalDateTime savedAt) {

  public static ArchiveWorkResult from(
      ArchiveWork archiveWork, String memo, ArtworkSummaryResult summary) {
    return new ArchiveWorkResult(
        summary == null ? null : summary.artworkImageUrl(),
        summary == null ? null : summary.artworkName(),
        summary == null ? null : summary.artistName(),
        memo,
        archiveWork.getId(),
        archiveWork.getDisplayArtworkId(),
        null,
        archiveWork.getUserId(),
        archiveWork.getSavedAt());
  }

  // 개인 작품 저장 기록은 전시 작품과 달리 별도 Aggregate(ArchivePersonalWork)에 저장되지만,
  // 응답은 하나의 목록으로 병합되므로 같은 Result 타입을 공유한다.
  // 요약 정보(PersonalArtworkSummaryResult)에 작가명이 없어 artistName은 항상 null이다.
  public static ArchiveWorkResult fromPersonal(
      ArchivePersonalWork archivePersonalWork, String memo, PersonalArtworkSummaryResult summary) {
    return new ArchiveWorkResult(
        summary == null ? null : summary.thumbnailUrl(),
        summary == null ? null : summary.artworkName(),
        null,
        memo,
        archivePersonalWork.getId(),
        null,
        archivePersonalWork.getPersonalArtworkId(),
        archivePersonalWork.getUserId(),
        archivePersonalWork.getSavedAt());
  }
}
