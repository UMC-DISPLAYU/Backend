package com.example.demo.domain.display.application.command;

import com.example.demo.domain.display.application.port.DisplayListCacheEvictionPort;
import com.example.demo.domain.display.application.service.DisplayContentPublicationService;
import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.entity.DisplayScreening;
import com.example.demo.domain.display.domain.error.DisplayErrorCode;
import com.example.demo.domain.display.domain.repository.DisplayRepository;
import com.example.demo.domain.display.domain.repository.DisplayScreeningRepository;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ApproveDisplayScreeningService {

  private final DisplayRepository displayRepository;
  private final DisplayScreeningRepository screeningRepository;
  private final DisplayContentPublicationService publicationService;
  private final DisplayListCacheEvictionPort cacheEvictionPort;
  private final Clock clock;

  public ApproveDisplayScreeningService(
      DisplayRepository displayRepository,
      DisplayScreeningRepository screeningRepository,
      DisplayContentPublicationService publicationService,
      DisplayListCacheEvictionPort cacheEvictionPort,
      Clock clock) {
    this.displayRepository = displayRepository;
    this.screeningRepository = screeningRepository;
    this.publicationService = publicationService;
    this.cacheEvictionPort = cacheEvictionPort;
    this.clock = clock;
  }

  @Transactional
  public void approve(Long displayId, Long reviewerId) {
    Display display = findDisplay(displayId);
    display.approveReview();
    DisplayScreening screening = findLatestScreening(displayId);
    LocalDateTime processedAt = LocalDateTime.ofInstant(clock.instant(), ZoneOffset.UTC);

    screening.approve(reviewerId, processedAt);
    publicationService.publishForDisplay(displayId);
    cacheEvictionPort.evictAfterCommit();
  }

  private Display findDisplay(Long displayId) {
    return displayRepository
        .findByIdWithOptimisticLock(displayId)
        .filter(candidate -> !candidate.isDeleted())
        .orElseThrow(() -> new BusinessException(GlobalErrorCode.NOT_FOUND));
  }

  private DisplayScreening findLatestScreening(Long displayId) {
    return screeningRepository
        .findLatestByDisplayId(displayId)
        .orElseThrow(() -> new BusinessException(DisplayErrorCode.DISPLAY_SCREENING_NOT_FOUND));
  }
}
