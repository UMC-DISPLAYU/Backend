package com.example.demo.domain.display.application.command;

import com.example.demo.domain.display.application.result.DisplayLikeResult;
import com.example.demo.domain.display.domain.entity.DisplayLike;
import com.example.demo.domain.display.domain.repository.DisplayLikeRepository;
import com.example.demo.domain.display.domain.repository.DisplayRepository;
import com.example.demo.domain.display.domain.vo.UserId;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import java.util.Objects;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DisplayLikeCommandService {

  private final DisplayRepository displayRepository;
  private final DisplayLikeRepository displayLikeRepository;

  public DisplayLikeCommandService(
      DisplayRepository displayRepository, DisplayLikeRepository displayLikeRepository) {
    this.displayRepository = displayRepository;
    this.displayLikeRepository = displayLikeRepository;
  }

  @Transactional
  public DisplayLikeResult like(DisplayLikeCommand command) {
    Objects.requireNonNull(command, "command must not be null.");
    validateDisplayExists(command.displayId());
    UserId userId = new UserId(command.userId());

    DisplayLike displayLike =
        displayLikeRepository
            .findByDisplayIdAndUserId(command.displayId(), userId)
            .map(this::restoreDeletedLike)
            .orElseGet(() -> DisplayLike.create(command.displayId(), userId));

    try {
      displayLikeRepository.save(displayLike);
    } catch (DataIntegrityViolationException exception) {
      throw new BusinessException(GlobalErrorCode.DUPLICATE_RESOURCE, exception);
    }

    return result(command.displayId());
  }

  @Transactional
  public DisplayLikeResult cancel(DisplayLikeCommand command) {
    Objects.requireNonNull(command, "command must not be null.");
    validateDisplayExists(command.displayId());
    UserId userId = new UserId(command.userId());

    DisplayLike displayLike =
        displayLikeRepository
            .findByDisplayIdAndUserId(command.displayId(), userId)
            .orElseThrow(() -> new BusinessException(GlobalErrorCode.NOT_FOUND));

    displayLike.cancel();
    return result(command.displayId());
  }

  private DisplayLike restoreDeletedLike(DisplayLike displayLike) {
    if (displayLike.isActive()) {
      throw new BusinessException(GlobalErrorCode.DUPLICATE_RESOURCE);
    }
    displayLike.restore();
    return displayLike;
  }

  private void validateDisplayExists(Long displayId) {
    displayRepository
        .findById(displayId)
        .orElseThrow(() -> new BusinessException(GlobalErrorCode.NOT_FOUND));
  }

  private DisplayLikeResult result(Long displayId) {
    return new DisplayLikeResult(
        displayId, displayLikeRepository.countByDisplayIdAndDeletedAtIsNull(displayId));
  }
}
