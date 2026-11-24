package com.example.demo.domain.display.application.query;

import com.example.demo.domain.display.application.result.DisplayDetailResult;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import java.time.Clock;
import java.time.LocalDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetDisplayDetailService {

  private final DisplayDetailQueryRepository displayDetailQueryRepository;
  private final Clock clock;

  public GetDisplayDetailService(
      DisplayDetailQueryRepository displayDetailQueryRepository, Clock clock) {
    this.displayDetailQueryRepository = displayDetailQueryRepository;
    this.clock = clock;
  }

  @Transactional(readOnly = true)
  public DisplayDetailResult getDisplayDetail(Long displayId, Long requesterUserId) {
    return displayDetailQueryRepository
        .findDisplayDetail(displayId, requesterUserId, LocalDate.now(clock))
        .orElseThrow(() -> new BusinessException(GlobalErrorCode.NOT_FOUND));
  }
}
