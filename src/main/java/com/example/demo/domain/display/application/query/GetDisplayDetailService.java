package com.example.demo.domain.display.application.query;

import com.example.demo.domain.display.application.result.DisplayDetailResult;
import com.example.demo.domain.display.domain.repository.DisplayLikeRepository;
import com.example.demo.domain.display.domain.repository.DisplayRepository;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetDisplayDetailService {

  private final DisplayRepository displayRepository;
  private final DisplayLikeRepository displayLikeRepository;

  public GetDisplayDetailService(
      DisplayRepository displayRepository, DisplayLikeRepository displayLikeRepository) {
    this.displayRepository = displayRepository;
    this.displayLikeRepository = displayLikeRepository;
  }

  @Transactional(readOnly = true)
  public DisplayDetailResult getDisplayDetail(Long displayId) {
    return displayRepository
        .findById(displayId)
        .map(
            display ->
                DisplayDetailResult.from(
                    display, displayLikeRepository.countByDisplayIdAndDeletedAtIsNull(displayId)))
        .orElseThrow(() -> new BusinessException(GlobalErrorCode.NOT_FOUND));
  }
}
