package com.example.demo.domain.displayartwork.application.command;

import com.example.demo.domain.displayartwork.application.result.DisplayArtworkLikeResult;
import com.example.demo.domain.displayartwork.domain.entity.DisplayArtworkLike;
import com.example.demo.domain.displayartwork.domain.error.DisplayArtworkErrorCode;
import com.example.demo.domain.displayartwork.domain.repository.DisplayArtworkLikeRepository;
import com.example.demo.domain.displayartwork.domain.repository.DisplayArtworkRepository;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import java.util.Objects;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DisplayArtworkLikeCommandService {

  private final DisplayArtworkRepository displayArtworkRepository;
  private final DisplayArtworkLikeRepository displayArtworkLikeRepository;

  public DisplayArtworkLikeCommandService(
      DisplayArtworkRepository displayArtworkRepository,
      DisplayArtworkLikeRepository displayArtworkLikeRepository) {
    this.displayArtworkRepository = displayArtworkRepository;
    this.displayArtworkLikeRepository = displayArtworkLikeRepository;
  }

  @Transactional
  public DisplayArtworkLikeResult like(DisplayArtworkLikeCommand command) {
    Objects.requireNonNull(command, "command must not be null.");
    validateDisplayArtworkExists(command.displayArtworkId());

    DisplayArtworkLike displayArtworkLike =
        displayArtworkLikeRepository
            .findByDisplayArtworkIdAndUserId(command.displayArtworkId(), command.userId())
            .map(this::restoreDeletedLike)
            .orElseGet(
                () -> DisplayArtworkLike.create(command.displayArtworkId(), command.userId()));

    try {
      displayArtworkLikeRepository.save(displayArtworkLike);
    } catch (DataIntegrityViolationException exception) {
      throw new BusinessException(GlobalErrorCode.DUPLICATE_RESOURCE, exception);
    }

    return result(command.displayArtworkId(), true);
  }

  @Transactional
  public DisplayArtworkLikeResult cancel(DisplayArtworkLikeCommand command) {
    Objects.requireNonNull(command, "command must not be null.");
    validateDisplayArtworkExists(command.displayArtworkId());

    DisplayArtworkLike displayArtworkLike =
        displayArtworkLikeRepository
            .findByDisplayArtworkIdAndUserId(command.displayArtworkId(), command.userId())
            .orElseThrow(() -> new BusinessException(GlobalErrorCode.NOT_FOUND));

    displayArtworkLike.cancel();
    return result(command.displayArtworkId(), false);
  }

  private DisplayArtworkLike restoreDeletedLike(DisplayArtworkLike displayArtworkLike) {
    if (displayArtworkLike.isActive()) {
      throw new BusinessException(GlobalErrorCode.DUPLICATE_RESOURCE);
    }
    displayArtworkLike.restore();
    return displayArtworkLike;
  }

  private void validateDisplayArtworkExists(Long displayArtworkId) {
    displayArtworkRepository
        .findById(displayArtworkId)
        .filter(artwork -> !artwork.isDeleted())
        .orElseThrow(
            () -> new BusinessException(DisplayArtworkErrorCode.DISPLAY_ARTWORK_NOT_FOUND));
  }

  private DisplayArtworkLikeResult result(Long displayArtworkId, boolean isLiked) {
    return new DisplayArtworkLikeResult(
        displayArtworkId,
        isLiked,
        displayArtworkLikeRepository.countByDisplayArtworkIdAndDeletedAtIsNull(displayArtworkId));
  }
}
