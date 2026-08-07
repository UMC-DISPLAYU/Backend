package com.example.demo.domain.display.application.service;

import com.example.demo.domain.archive.domain.repository.ArchiveDisplayRepository;
import com.example.demo.domain.display.application.result.ClosingSoonDisplayResult;
import com.example.demo.domain.display.application.result.DisplayDetailResult;
import com.example.demo.domain.display.application.result.DisplayMapResult;
import com.example.demo.domain.display.application.result.GraduationDisplayResult;
import com.example.demo.domain.display.application.result.SearchDisplayResult;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DisplayBookmarkEnrichmentService {

  private final ArchiveDisplayRepository archiveDisplayRepository;

  public DisplayBookmarkEnrichmentService(ArchiveDisplayRepository archiveDisplayRepository) {
    this.archiveDisplayRepository = archiveDisplayRepository;
  }

  @Transactional(readOnly = true)
  public DisplayDetailResult enrich(DisplayDetailResult result, Long userId) {
    if (userId == null) {
      return result.withArchived(false);
    }
    boolean isArchived =
        archiveDisplayRepository.findByUserIdAndDisplayId(userId, result.displayId()).isPresent();
    return result.withArchived(isArchived);
  }

  @Transactional(readOnly = true)
  public SearchDisplayResult enrich(SearchDisplayResult result, Long userId) {
    Set<Long> archivedDisplayIds =
        findArchivedDisplayIds(
            userId,
            result.exhibitions().stream()
                .map(SearchDisplayResult.ExhibitionResult::displayId)
                .toList());
    return new SearchDisplayResult(
        result.exhibitions().stream()
            .map(
                exhibition ->
                    exhibition.withArchived(archivedDisplayIds.contains(exhibition.displayId())))
            .toList(),
        result.pagination());
  }

  @Transactional(readOnly = true)
  public ClosingSoonDisplayResult enrich(ClosingSoonDisplayResult result, Long userId) {
    Set<Long> archivedDisplayIds =
        findArchivedDisplayIds(
            userId,
            result.exhibitions().stream()
                .map(ClosingSoonDisplayResult.ExhibitionResult::displayId)
                .toList());
    return new ClosingSoonDisplayResult(
        result.exhibitions().stream()
            .map(
                exhibition ->
                    exhibition.withArchived(archivedDisplayIds.contains(exhibition.displayId())))
            .toList(),
        result.pagination());
  }

  @Transactional(readOnly = true)
  public GraduationDisplayResult enrich(GraduationDisplayResult result, Long userId) {
    Set<Long> archivedDisplayIds =
        findArchivedDisplayIds(
            userId,
            result.exhibitions().stream()
                .map(GraduationDisplayResult.ExhibitionResult::displayId)
                .toList());
    return new GraduationDisplayResult(
        result.exhibitions().stream()
            .map(
                exhibition ->
                    exhibition.withArchived(archivedDisplayIds.contains(exhibition.displayId())))
            .toList());
  }

  @Transactional(readOnly = true)
  public DisplayMapResult enrich(DisplayMapResult result, Long userId) {
    Set<Long> archivedDisplayIds =
        findArchivedDisplayIds(
            userId,
            result.markers().stream().map(DisplayMapResult.MarkerResult::displayId).toList());
    return new DisplayMapResult(
        result.markers().stream()
            .map(marker -> marker.withArchived(archivedDisplayIds.contains(marker.displayId())))
            .toList(),
        result.pagination());
  }

  private Set<Long> findArchivedDisplayIds(Long userId, List<Long> displayIds) {
    if (userId == null || displayIds.isEmpty()) {
      return Set.of();
    }
    return new HashSet<>(
        archiveDisplayRepository.findDisplayIdsByUserIdAndDisplayIdIn(userId, displayIds));
  }
}
