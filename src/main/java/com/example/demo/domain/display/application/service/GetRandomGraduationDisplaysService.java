package com.example.demo.domain.display.application.service;

import com.example.demo.domain.display.application.cache.DisplayCacheNames;
import com.example.demo.domain.display.application.query.GraduationDisplayQueryRepository;
import com.example.demo.domain.display.application.result.GraduationDisplayResult;
import com.example.demo.domain.display.application.result.GraduationDisplayResult.ExhibitionResult;
import com.example.demo.domain.display.application.usecase.GetRandomGraduationDisplaysUseCase;
import java.time.Clock;
import java.time.LocalDate;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetRandomGraduationDisplaysService implements GetRandomGraduationDisplaysUseCase {

  private final GraduationDisplayQueryRepository queryRepository;
  private final Clock clock;

  public GetRandomGraduationDisplaysService(
      GraduationDisplayQueryRepository queryRepository, Clock clock) {
    this.queryRepository = queryRepository;
    this.clock = clock;
  }

  @Override
  @Cacheable(cacheNames = DisplayCacheNames.GRADUATION, key = "#size")
  @Transactional(readOnly = true)
  public GraduationDisplayResult getRandomGraduationDisplays(int size) {
    LocalDate today = LocalDate.now(clock);
    return new GraduationDisplayResult(
        queryRepository.findRandomGraduationDisplays(size).stream()
            .map(queryResult -> ExhibitionResult.from(queryResult, today))
            .toList());
  }
}
