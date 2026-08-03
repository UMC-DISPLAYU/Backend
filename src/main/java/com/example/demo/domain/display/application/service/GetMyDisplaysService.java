package com.example.demo.domain.display.application.service;

import com.example.demo.domain.display.application.result.MyDisplayListResult;
import com.example.demo.domain.display.domain.repository.DisplayRepository;
import java.time.Clock;
import java.time.LocalDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetMyDisplaysService {

  private final DisplayRepository displayRepository;
  private final Clock clock;

  public GetMyDisplaysService(DisplayRepository displayRepository, Clock clock) {
    this.displayRepository = displayRepository;
    this.clock = clock;
  }

  @Transactional(readOnly = true)
  public MyDisplayListResult getMyDisplays(Long userId) {
    return MyDisplayListResult.from(
        displayRepository.findCreatedDisplaysByUserId(userId),
        displayRepository.findParticipatedDisplaysByUserId(userId),
        LocalDate.now(clock));
  }
}
