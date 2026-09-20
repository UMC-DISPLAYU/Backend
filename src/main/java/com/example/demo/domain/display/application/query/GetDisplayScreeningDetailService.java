package com.example.demo.domain.display.application.query;

import com.example.demo.domain.display.application.result.DisplayScreeningDetailResult;
import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.entity.DisplayScreening;
import com.example.demo.domain.display.domain.repository.DisplayRepository;
import com.example.demo.domain.display.domain.repository.DisplayScreeningRepository;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetDisplayScreeningDetailService {

  private final DisplayRepository displayRepository;
  private final DisplayScreeningRepository screeningRepository;

  public GetDisplayScreeningDetailService(
      DisplayRepository displayRepository, DisplayScreeningRepository screeningRepository) {
    this.displayRepository = displayRepository;
    this.screeningRepository = screeningRepository;
  }

  @Transactional(readOnly = true)
  public DisplayScreeningDetailResult getDetail(Long displayId) {
    Display display =
        displayRepository
            .findById(displayId)
            .filter(candidate -> !candidate.isDeleted())
            .orElseThrow(() -> new BusinessException(GlobalErrorCode.NOT_FOUND));
    DisplayScreening screening = screeningRepository.findLatestByDisplayId(displayId).orElse(null);
    return DisplayScreeningDetailResult.from(display, screening);
  }
}
