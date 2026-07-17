package com.example.demo.domain.archive.presentation.mapper;

import com.example.demo.domain.archive.application.result.ArchiveArtistCursorResult;
import com.example.demo.domain.archive.application.result.ArchiveArtistResult;
import com.example.demo.domain.archive.application.result.ArchiveArtistToggleResult;
import com.example.demo.domain.archive.application.result.ArchiveDisplayCursorResult;
import com.example.demo.domain.archive.application.result.ArchiveDisplayResult;
import com.example.demo.domain.archive.application.result.ArchiveDisplayToggleResult;
import com.example.demo.domain.archive.application.result.ArchiveWorkCursorResult;
import com.example.demo.domain.archive.application.result.ArchiveWorkResult;
import com.example.demo.domain.archive.application.result.ArchiveWorkToggleResult;
import com.example.demo.domain.archive.presentation.response.ArchiveArtistCursorResponse;
import com.example.demo.domain.archive.presentation.response.ArchiveArtistResponse;
import com.example.demo.domain.archive.presentation.response.ArchiveArtistToggleResponse;
import com.example.demo.domain.archive.presentation.response.ArchiveDisplayCursorResponse;
import com.example.demo.domain.archive.presentation.response.ArchiveDisplayResponse;
import com.example.demo.domain.archive.presentation.response.ArchiveDisplayToggleResponse;
import com.example.demo.domain.archive.presentation.response.ArchiveWorkCursorResponse;
import com.example.demo.domain.archive.presentation.response.ArchiveWorkResponse;
import com.example.demo.domain.archive.presentation.response.ArchiveWorkToggleResponse;
import org.springframework.stereotype.Component;

@Component
public class ArchivePresentationMapper {

  public ArchiveDisplayResponse toResponse(ArchiveDisplayResult result) {
    return new ArchiveDisplayResponse(
        result.archiveDisplayId(),
        result.displayId(),
        result.userId(),
        result.memo(),
        result.savedAt());
  }

  public ArchiveDisplayToggleResponse toResponse(ArchiveDisplayToggleResult result) {
    // 도메인 용어(displayId) -> API 응답 용어(exhibitionId) 변환은 여기(Presentation Mapper)에서만 담당
    return new ArchiveDisplayToggleResponse(result.displayId(), result.isArchived());
  }

  public ArchiveDisplayCursorResponse toResponse(ArchiveDisplayCursorResult result) {
    return new ArchiveDisplayCursorResponse(
        result.displays().stream().map(this::toResponse).toList(),
        result.nextCursorId(),
        result.size(),
        result.hasNext());
  }

  public ArchiveWorkResponse toResponse(ArchiveWorkResult result) {
    return new ArchiveWorkResponse(
        result.archiveWorkId(),
        result.displayArtworkId(),
        result.userId(),
        result.memo(),
        result.savedAt());
  }

  public ArchiveWorkToggleResponse toResponse(ArchiveWorkToggleResult result) {
    // 도메인 용어(displayArtworkId) -> API 응답 용어(artworkId) 변환은 여기(Presentation Mapper)에서만 담당
    return new ArchiveWorkToggleResponse(result.displayArtworkId(), result.isArchived());
  }

  public ArchiveWorkCursorResponse toResponse(ArchiveWorkCursorResult result) {
    return new ArchiveWorkCursorResponse(
        result.works().stream().map(this::toResponse).toList(),
        result.nextCursorId(),
        result.size(),
        result.hasNext());
  }

  public ArchiveArtistResponse toResponse(ArchiveArtistResult result) {
    // 도메인 용어(artistProfileId) -> API 응답 용어(artistId) 변환은 여기(Presentation Mapper)에서만 담당
    return new ArchiveArtistResponse(
        result.archiveArtistId(), result.artistProfileId(), result.userId(), result.savedAt());
  }

  public ArchiveArtistToggleResponse toResponse(ArchiveArtistToggleResult result) {
    // 도메인 용어(artistProfileId) -> API 응답 용어(artistId) 변환은 여기(Presentation Mapper)에서만 담당
    return new ArchiveArtistToggleResponse(result.artistProfileId(), result.isArchived());
  }

  public ArchiveArtistCursorResponse toResponse(ArchiveArtistCursorResult result) {
    return new ArchiveArtistCursorResponse(
        result.artists().stream().map(this::toResponse).toList(),
        result.nextCursorId(),
        result.size(),
        result.hasNext());
  }
}
