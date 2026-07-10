package com.example.demo.domain.archive.presentation.mapper;

import com.example.demo.domain.archive.application.result.ArchiveDisplayResult;
import com.example.demo.domain.archive.application.result.ArchiveDisplayToggleResult;
import com.example.demo.domain.archive.presentation.response.ArchiveDisplayResponse;
import com.example.demo.domain.archive.presentation.response.ArchiveDisplayToggleResponse;
import org.springframework.stereotype.Component;

@Component
public class ArchivePresentationMapper {

  public ArchiveDisplayResponse toResponse(ArchiveDisplayResult result) {
    return new ArchiveDisplayResponse(
        result.archiveDisplayId(), result.displayId(), result.userId());
  }

  public ArchiveDisplayToggleResponse toResponse(ArchiveDisplayToggleResult result) {
    return new ArchiveDisplayToggleResponse(result.exhibitionId(), result.isArchived());
  }
}
