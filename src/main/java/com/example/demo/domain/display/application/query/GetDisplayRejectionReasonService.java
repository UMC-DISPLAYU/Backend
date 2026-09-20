package com.example.demo.domain.display.application.query;

import com.example.demo.domain.display.application.permission.DisplayPermissionChecker;
import com.example.demo.domain.display.application.result.DisplayRejectionReasonResult;
import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.entity.DisplayScreening;
import com.example.demo.domain.display.domain.error.DisplayErrorCode;
import com.example.demo.domain.display.domain.repository.DisplayRepository;
import com.example.demo.domain.display.domain.repository.DisplayScreeningRepository;
import com.example.demo.domain.display.domain.type.DisplayStatus;
import com.example.demo.global.error.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetDisplayRejectionReasonService {

  private final DisplayRepository displayRepository;
  private final DisplayScreeningRepository screeningRepository;
  private final DisplayPermissionChecker displayPermissionChecker;

  public GetDisplayRejectionReasonService(
      DisplayRepository displayRepository,
      DisplayScreeningRepository screeningRepository,
      DisplayPermissionChecker displayPermissionChecker) {
    this.displayRepository = displayRepository;
    this.screeningRepository = screeningRepository;
    this.displayPermissionChecker = displayPermissionChecker;
  }

  @Transactional(readOnly = true)
  public DisplayRejectionReasonResult getRejectionReason(Long displayId, Long userId) {
    Display display =
        displayRepository
            .findById(displayId)
            .filter(candidate -> !candidate.isDeleted())
            .orElseThrow(() -> new BusinessException(DisplayErrorCode.DISPLAY_NOT_FOUND));
    displayPermissionChecker.requireAcceptedTeamMember(display, userId);
    if (display.getStatus() != DisplayStatus.REJECTED) {
      throw new BusinessException(DisplayErrorCode.DISPLAY_REJECTION_REASON_UNAVAILABLE);
    }

    DisplayScreening screening =
        screeningRepository
            .findLatestByDisplayId(displayId)
            .orElseThrow(() -> new BusinessException(DisplayErrorCode.DISPLAY_SCREENING_NOT_FOUND));
    return new DisplayRejectionReasonResult(
        display.getId(), display.getStatus(), screening.getRejectionReason());
  }
}
