package com.example.demo.domain.archive.infrastructure.persistence;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArchiveDisplayImageSummaryJpaRepository
    extends JpaRepository<ArchiveDisplayImageReferenceJpaEntity, Long> {

  List<ArchiveDisplayImageReferenceJpaEntity>
      findByDisplayIdInAndImageTypeAndSortOrderAndDeletedAtIsNull(
          List<Long> displayIds, String imageType, int sortOrder);
}
