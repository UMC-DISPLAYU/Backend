package com.example.demo.domain.archive.presentation.mapper;

import com.example.demo.domain.archive.application.result.ArchiveArtistCursorResult;
import com.example.demo.domain.archive.application.result.ArchiveArtistResult;
import com.example.demo.domain.archive.application.result.ArchiveArtistToggleResult;
import com.example.demo.domain.archive.application.result.ArchiveDisplayResult;
import com.example.demo.domain.archive.application.result.ArchiveDisplayToggleResult;
import com.example.demo.domain.archive.presentation.response.ArchiveArtistCursorResponse;
import com.example.demo.domain.archive.presentation.response.ArchiveArtistResponse;
import com.example.demo.domain.archive.presentation.response.ArchiveArtistToggleResponse;
import com.example.demo.domain.archive.presentation.response.ArchiveDisplayResponse;
import com.example.demo.domain.archive.presentation.response.ArchiveDisplayToggleResponse;
import org.springframework.stereotype.Component;

@Component
public class ArchivePresentationMapper {

  public ArchiveDisplayResponse toResponse(ArchiveDisplayResult result) {
    return new ArchiveDisplayResponse(
        result.archiveDisplayId(), result.displayId(), result.userId(), result.savedAt());
  }

  public ArchiveDisplayToggleResponse toResponse(ArchiveDisplayToggleResult result) {
    // 도메인 용어(displayId) -> API 응답 용어(exhibitionId) 변환은 여기(Presentation Mapper)에서만 담당
    return new ArchiveDisplayToggleResponse(result.displayId(), result.isArchived());
  }

  public ArchiveArtistResponse toResponse(ArchiveArtistResult result) {
    return new ArchiveArtistResponse(
        result.archiveArtistId(), result.creatorId(), result.userId(), result.savedAt());
  }

  public ArchiveArtistToggleResponse toResponse(ArchiveArtistToggleResult result) {
    // 도메인 용어(creatorId) -> API 응답 용어(artistId) 변환은 여기(Presentation Mapper)에서만 담당
    return new ArchiveArtistToggleResponse(result.creatorId(), result.isArchived());
  }

  public ArchiveArtistCursorResponse toResponse(ArchiveArtistCursorResult result) {
    return new ArchiveArtistCursorResponse(
        result.artists().stream().map(this::toResponse).toList(),
        result.nextCursorId(),
        result.size(),
        result.hasNext());
  }
}
