package com.example.demo.domain.displayartwork.application.command;

import com.example.demo.domain.displayartwork.application.result.DisplayArtworkLikeResult;
import com.example.demo.domain.displayartwork.domain.entity.DisplayArtworkLike;
import com.example.demo.domain.displayartwork.domain.error.DisplayArtworkErrorCode;
import com.example.demo.domain.displayartwork.domain.repository.DisplayArtworkLikeRepository;
import com.example.demo.domain.displayartwork.domain.repository.DisplayArtworkRepository;
import com.example.demo.global.error.BusinessException;
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

    if (displayArtworkLikeRepository
        .findByDisplayArtworkIdAndUserId(command.displayArtworkId(), command.userId())
        .isPresent()) {
      return result(command.displayArtworkId(), true);
    }

    DisplayArtworkLike displayArtworkLike =
        DisplayArtworkLike.create(command.displayArtworkId(), command.userId());

    try {
      displayArtworkLikeRepository.save(displayArtworkLike);
    } catch (DataIntegrityViolationException exception) {
      return result(command.displayArtworkId(), true);
    }

    return result(command.displayArtworkId(), true);
  }

  @Transactional
  public DisplayArtworkLikeResult cancel(DisplayArtworkLikeCommand command) {
    Objects.requireNonNull(command, "command must not be null.");
    validateDisplayArtworkExists(command.displayArtworkId());

    int deleted =
        displayArtworkLikeRepository.deleteByDisplayArtworkIdAndUserId(
            command.displayArtworkId(), command.userId());
    if (deleted == 0) {
      throw new BusinessException(DisplayArtworkErrorCode.DISPLAY_ARTWORK_LIKE_NOT_FOUND);
    }
    return result(command.displayArtworkId(), false);
  }

  private void validateDisplayArtworkExists(Long displayArtworkId) {
    displayArtworkRepository
        .findById(displayArtworkId)
        .filter(artwork -> !artwork.isDeleted() && !artwork.getDisplay().isDeleted())
        .orElseThrow(
            () -> new BusinessException(DisplayArtworkErrorCode.DISPLAY_ARTWORK_NOT_FOUND));
  }

  private DisplayArtworkLikeResult result(Long displayArtworkId, boolean isLiked) {
    return new DisplayArtworkLikeResult(
        displayArtworkId,
        isLiked,
        displayArtworkLikeRepository.countByDisplayArtworkId(displayArtworkId));
  }
}
