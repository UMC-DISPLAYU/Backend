package com.example.demo.domain.archive.presentation.mapper;

import com.example.demo.domain.archive.application.result.ArchiveDisplayResult;
import com.example.demo.domain.archive.application.result.ArchiveDisplayToggleResult;
import com.example.demo.domain.archive.application.result.ArchiveWorkResult;
import com.example.demo.domain.archive.application.result.ArchiveWorkToggleResult;
import com.example.demo.domain.archive.presentation.response.ArchiveDisplayResponse;
import com.example.demo.domain.archive.presentation.response.ArchiveDisplayToggleResponse;
import com.example.demo.domain.archive.presentation.response.ArchiveWorkResponse;
import com.example.demo.domain.archive.presentation.response.ArchiveWorkToggleResponse;
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

  public ArchiveWorkResponse toResponse(ArchiveWorkResult result) {
    return new ArchiveWorkResponse(
        result.archiveWorkId(), result.displayArtworkId(), result.userId(), result.savedAt());
  }

  public ArchiveWorkToggleResponse toResponse(ArchiveWorkToggleResult result) {
    // 도메인 용어(displayArtworkId) -> API 응답 용어(artworkId) 변환은 여기(Presentation Mapper)에서만 담당
    return new ArchiveWorkToggleResponse(result.displayArtworkId(), result.isArchived());
  }
}
