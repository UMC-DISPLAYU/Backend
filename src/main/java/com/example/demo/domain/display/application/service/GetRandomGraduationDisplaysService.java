package com.example.demo.domain.display.application.service;

import com.example.demo.domain.display.application.query.GraduationDisplayQueryRepository;
import com.example.demo.domain.display.application.result.ClosingSoonDisplayResult;
import com.example.demo.domain.display.application.result.ClosingSoonDisplayResult.ExhibitionResult;
import com.example.demo.domain.display.application.usecase.GetRandomGraduationDisplaysUseCase;
import java.time.LocalDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetRandomGraduationDisplaysService implements GetRandomGraduationDisplaysUseCase {

  private final GraduationDisplayQueryRepository queryRepository;

  public GetRandomGraduationDisplaysService(GraduationDisplayQueryRepository queryRepository) {
    this.queryRepository = queryRepository;
  }

  @Override
  @Transactional(readOnly = true)
  public ClosingSoonDisplayResult getRandomGraduationDisplays(int size) {
    LocalDate today = LocalDate.now();
    return new ClosingSoonDisplayResult(
        queryRepository.findRandomGraduationDisplays(size).stream()
            .map(queryResult -> ExhibitionResult.from(queryResult, today))
            .toList());
  }
}
