package com.example.demo.domain.display.application.query;

import com.example.demo.domain.display.application.result.DisplayLikeStatusResult;
import com.example.demo.domain.display.domain.repository.DisplayLikeRepository;
import com.example.demo.domain.display.domain.repository.DisplayRepository;
import com.example.demo.domain.display.domain.vo.UserId;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetDisplayLikeStatusService {

  private final DisplayRepository displayRepository;
  private final DisplayLikeRepository displayLikeRepository;

  public GetDisplayLikeStatusService(
      DisplayRepository displayRepository, DisplayLikeRepository displayLikeRepository) {
    this.displayRepository = displayRepository;
    this.displayLikeRepository = displayLikeRepository;
  }

  @Transactional(readOnly = true)
  public DisplayLikeStatusResult getLikeStatus(Long displayId, Long userId) {
    displayRepository
        .findById(displayId)
        .orElseThrow(() -> new BusinessException(GlobalErrorCode.NOT_FOUND));

    boolean isLiked =
        displayLikeRepository
            .findByDisplayIdAndUserId(displayId, new UserId(userId))
            .filter(displayLike -> displayLike.isActive())
            .isPresent();
    return new DisplayLikeStatusResult(isLiked);
  }
}
