package com.example.demo.domain.display.application.query;

import com.example.demo.domain.display.application.result.DisplayDetailResult;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetDisplayDetailService {

  private final DisplayDetailQueryRepository displayDetailQueryRepository;

  public GetDisplayDetailService(DisplayDetailQueryRepository displayDetailQueryRepository) {
    this.displayDetailQueryRepository = displayDetailQueryRepository;
  }

  @Transactional(readOnly = true)
  public DisplayDetailResult getDisplayDetail(Long displayId, Long requesterUserId) {
    return displayDetailQueryRepository
        .findDisplayDetail(displayId, requesterUserId)
        .orElseThrow(() -> new BusinessException(GlobalErrorCode.NOT_FOUND));
  }
}
