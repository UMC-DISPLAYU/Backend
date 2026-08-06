package com.example.demo.domain.display.application.service;

import com.example.demo.domain.display.application.query.DisplayInvitationDisplayQueryRepository;
import com.example.demo.domain.display.application.result.GraduationDisplayResult;
import java.time.Clock;
import java.time.LocalDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetDisplayInvitationDisplaysService {

  private final DisplayInvitationDisplayQueryRepository queryRepository;
  private final Clock clock;

  public GetDisplayInvitationDisplaysService(
      DisplayInvitationDisplayQueryRepository queryRepository, Clock clock) {
    this.queryRepository = queryRepository;
    this.clock = clock;
  }

  @Transactional(readOnly = true)
  public GraduationDisplayResult getInvitations(Long requesterUserId) {
    LocalDate today = LocalDate.now(clock);
    return new GraduationDisplayResult(
        queryRepository.findPendingInvitationDisplays(requesterUserId).stream()
            .map(queryResult -> GraduationDisplayResult.ExhibitionResult.from(queryResult, today))
            .toList());
  }
}
