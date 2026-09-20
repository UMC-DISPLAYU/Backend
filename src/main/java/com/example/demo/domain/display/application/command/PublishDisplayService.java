package com.example.demo.domain.display.application.command;

import com.example.demo.domain.display.application.permission.DisplayPermissionChecker;
import com.example.demo.domain.display.application.result.DisplayDetailResult;
import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.entity.DisplayScreening;
import com.example.demo.domain.display.domain.repository.DisplayLikeRepository;
import com.example.demo.domain.display.domain.repository.DisplayRepository;
import com.example.demo.domain.display.domain.repository.DisplayScreeningRepository;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PublishDisplayService {

  private final DisplayRepository displayRepository;
  private final DisplayLikeRepository displayLikeRepository;
  private final DisplayScreeningRepository screeningRepository;
  private final DisplayPermissionChecker displayPermissionChecker;
  private final Clock clock;

  public PublishDisplayService(
      DisplayRepository displayRepository,
      DisplayLikeRepository displayLikeRepository,
      DisplayScreeningRepository screeningRepository,
      DisplayPermissionChecker displayPermissionChecker,
      Clock clock) {
    this.displayRepository = displayRepository;
    this.displayLikeRepository = displayLikeRepository;
    this.screeningRepository = screeningRepository;
    this.displayPermissionChecker = displayPermissionChecker;
    this.clock = clock;
  }

  @Transactional
  public DisplayDetailResult publishDisplay(PublishDisplayCommand command) {
    Objects.requireNonNull(command, "command must not be null.");

    Display display =
        displayRepository
            .findByIdWithOptimisticLock(command.displayId())
            .filter(candidate -> !candidate.isDeleted())
            .orElseThrow(() -> new BusinessException(GlobalErrorCode.NOT_FOUND));
    displayPermissionChecker.requireTeamLeader(display, command.userId());

    display.requestReview();
    screeningRepository.save(
        DisplayScreening.request(
            display, command.userId(), LocalDateTime.ofInstant(clock.instant(), ZoneOffset.UTC)));
    return DisplayDetailResult.from(
        display, displayLikeRepository.countByDisplayId(display.getId()));
  }
}
