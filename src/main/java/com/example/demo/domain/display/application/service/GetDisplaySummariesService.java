package com.example.demo.domain.display.application.service;

import com.example.demo.domain.display.application.query.DisplaySummaryQueryRepository;
import com.example.demo.domain.display.application.result.DisplaySummaryResult;
import com.example.demo.domain.display.application.usecase.GetDisplaySummariesUseCase;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetDisplaySummariesService implements GetDisplaySummariesUseCase {

  private final DisplaySummaryQueryRepository queryRepository;

  public GetDisplaySummariesService(DisplaySummaryQueryRepository queryRepository) {
    this.queryRepository = queryRepository;
  }

  @Override
  @Transactional(readOnly = true)
  public List<DisplaySummaryResult> getDisplaySummaries(List<Long> displayIds) {
    if (displayIds.isEmpty()) {
      return List.of();
    }
    return queryRepository.findByDisplayIdIn(displayIds).stream()
        .map(DisplaySummaryResult::from)
        .toList();
  }
}
