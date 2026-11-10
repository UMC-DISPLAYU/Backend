package com.example.demo.domain.display.application.service;

import com.example.demo.domain.display.application.query.PopularDisplayQueryRepository;
import com.example.demo.domain.display.application.result.PopularDisplayResult;
import com.example.demo.domain.display.application.usecase.GetPopularDisplaysUseCase;
import java.time.Clock;
import java.time.LocalDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetPopularDisplaysService implements GetPopularDisplaysUseCase {
  private final PopularDisplayQueryRepository repository;
  private final Clock clock;

  public GetPopularDisplaysService(PopularDisplayQueryRepository repository, Clock clock) {
    this.repository = repository;
    this.clock = clock;
  }

  @Override
  @Transactional(readOnly = true)
  public PopularDisplayResult getPopularDisplays() {
    LocalDate today = LocalDate.now(clock);
    return new PopularDisplayResult(
        repository.findPopularDisplays(today).stream()
            .map(row -> PopularDisplayResult.ExhibitionResult.from(row, today))
            .toList());
  }
}
