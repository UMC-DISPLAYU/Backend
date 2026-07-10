package com.example.demo.domain.display.application.service;

import com.example.demo.domain.display.application.query.ClosingSoonDisplayQueryRepository;
import com.example.demo.domain.display.application.result.ClosingSoonDisplayResult;
import com.example.demo.domain.display.application.result.ClosingSoonDisplayResult.ExhibitionResult;
import com.example.demo.domain.display.application.usecase.GetClosingSoonDisplaysUseCase;
import java.time.LocalDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetClosingSoonDisplaysService implements GetClosingSoonDisplaysUseCase {

  private final ClosingSoonDisplayQueryRepository queryRepository;

  public GetClosingSoonDisplaysService(ClosingSoonDisplayQueryRepository queryRepository) {
    this.queryRepository = queryRepository;
  }

  @Override
  @Transactional(readOnly = true)
  public ClosingSoonDisplayResult getClosingSoonDisplays() {
    LocalDate today = LocalDate.now();
    return new ClosingSoonDisplayResult(
        queryRepository.findClosingSoonDisplays(today).stream()
            .map(queryResult -> ExhibitionResult.from(queryResult, today))
            .toList());
  }
}
