package com.example.demo.domain.display.application.command;

import com.example.demo.domain.display.application.permission.DisplayPermissionChecker;
import com.example.demo.domain.display.application.result.CancelDisplayScreeningResult;
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
public class CancelDisplayScreeningService {
  private final DisplayRepository displayRepository;
  private final DisplayScreeningRepository screeningRepository;
  private final DisplayPermissionChecker permissionChecker;
  private final Clock clock;

  public CancelDisplayScreeningService(
      DisplayRepository displayRepository,
      DisplayScreeningRepository screeningRepository,
      DisplayPermissionChecker permissionChecker,
      Clock clock) {
    this.displayRepository = displayRepository;
    this.screeningRepository = screeningRepository;
    this.permissionChecker = permissionChecker;
    this.clock = clock;
  }

  @Transactional
  public CancelDisplayScreeningResult cancel(Long displayId, Long screeningId, Long userId) {
    Display display =
        displayRepository
            .findByIdWithOptimisticLock(displayId)
            .filter(candidate -> !candidate.isDeleted())
            .orElseThrow(() -> new BusinessException(GlobalErrorCode.NOT_FOUND));
    permissionChecker.requireTeamLeader(display, userId);
    DisplayScreening screening =
        screeningRepository
            .findByIdAndDisplayId(screeningId, displayId)
            .orElseThrow(() -> new BusinessException(DisplayErrorCode.DISPLAY_SCREENING_NOT_FOUND));
    DisplayScreening latest =
        screeningRepository
            .findLatestByDisplayId(displayId)
            .orElseThrow(() -> new BusinessException(DisplayErrorCode.DISPLAY_SCREENING_NOT_FOUND));
    if (!screening.getId().equals(latest.getId())) {
      throw new BusinessException(DisplayErrorCode.INVALID_DISPLAY_REVIEW_STATUS);
    }
    display.cancelReview();
    screening.cancel(userId, LocalDateTime.ofInstant(clock.instant(), ZoneOffset.UTC));
    return new CancelDisplayScreeningResult(
        displayId, screeningId, display.getStatus().name(), screening.getStatus().name());
  }
}
