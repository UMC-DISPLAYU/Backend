package com.example.demo.domain.display.application.command;

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
public class RejectDisplayScreeningService {

  private final DisplayRepository displayRepository;
  private final DisplayScreeningRepository screeningRepository;
  private final Clock clock;

  public RejectDisplayScreeningService(
      DisplayRepository displayRepository,
      DisplayScreeningRepository screeningRepository,
      Clock clock) {
    this.displayRepository = displayRepository;
    this.screeningRepository = screeningRepository;
    this.clock = clock;
  }

  @Transactional
  public void reject(Long displayId, Long reviewerId, String reason) {
    Display display = findDisplay(displayId);
    display.rejectReview();
    DisplayScreening screening = findLatestScreening(displayId);
    LocalDateTime processedAt = LocalDateTime.ofInstant(clock.instant(), ZoneOffset.UTC);

    screening.reject(reviewerId, reason, processedAt);
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
