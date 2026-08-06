package com.example.demo.domain.archive.infrastructure.persistence.adapter;

import com.example.demo.domain.archive.domain.repository.DisplaySummary;
import com.example.demo.domain.archive.domain.repository.DisplaySummaryRepository;
import com.example.demo.domain.archive.infrastructure.persistence.ArchiveDisplayImageReferenceJpaEntity;
import com.example.demo.domain.archive.infrastructure.persistence.ArchiveDisplayImageSummaryJpaRepository;
import com.example.demo.domain.archive.infrastructure.persistence.ArchiveDisplayReferenceJpaEntity;
import com.example.demo.domain.archive.infrastructure.persistence.ArchiveDisplaySummaryJpaRepository;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class DisplaySummaryJpaAdapter implements DisplaySummaryRepository {

  private static final String MAIN_IMAGE_TYPE = "MAIN";
  private static final int MAIN_IMAGE_SORT_ORDER = 0;

  private final ArchiveDisplaySummaryJpaRepository displayJpaRepository;
  private final ArchiveDisplayImageSummaryJpaRepository imageJpaRepository;

  public DisplaySummaryJpaAdapter(
      ArchiveDisplaySummaryJpaRepository displayJpaRepository,
      ArchiveDisplayImageSummaryJpaRepository imageJpaRepository) {
    this.displayJpaRepository = displayJpaRepository;
    this.imageJpaRepository = imageJpaRepository;
  }

  @Override
  public List<DisplaySummary> findByDisplayIdIn(List<Long> displayIds) {
    if (displayIds.isEmpty()) {
      return List.of();
    }

    Map<Long, String> posterImageUrlByDisplayId =
        imageJpaRepository
            .findByDisplayIdInAndImageTypeAndSortOrderAndDeletedAtIsNull(
                displayIds, MAIN_IMAGE_TYPE, MAIN_IMAGE_SORT_ORDER)
            .stream()
            .collect(
                Collectors.toMap(
                    ArchiveDisplayImageReferenceJpaEntity::getDisplayId,
                    ArchiveDisplayImageReferenceJpaEntity::getImageUrl,
                    (existing, replacement) -> existing));

    return displayJpaRepository.findAllById(displayIds).stream()
        .map(toDisplaySummary(posterImageUrlByDisplayId))
        .toList();
  }

  private Function<ArchiveDisplayReferenceJpaEntity, DisplaySummary> toDisplaySummary(
      Map<Long, String> posterImageUrlByDisplayId) {
    return entity ->
        new DisplaySummary(
            entity.getDisplayId(),
            entity.getTitle(),
            entity.getOrganization(),
            entity.getDepartment(),
            entity.getPlaceName(),
            entity.getStartDate(),
            entity.getEndDate(),
            posterImageUrlByDisplayId.get(entity.getDisplayId()));
  }
}
