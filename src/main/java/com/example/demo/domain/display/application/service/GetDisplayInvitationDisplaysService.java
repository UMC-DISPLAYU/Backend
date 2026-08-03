package com.example.demo.domain.display.application.service;

import com.example.demo.domain.display.application.result.GraduationDisplayResult;
import com.example.demo.domain.display.domain.repository.DisplayInvitationRepository;
import java.time.Clock;
import java.time.LocalDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetDisplayInvitationDisplaysService {

  private final DisplayInvitationRepository invitationRepository;
  private final Clock clock;

  public GetDisplayInvitationDisplaysService(
      DisplayInvitationRepository invitationRepository, Clock clock) {
    this.invitationRepository = invitationRepository;
    this.clock = clock;
  }

  @Transactional(readOnly = true)
  public GraduationDisplayResult getInvitations(Long requesterUserId) {
    LocalDate today = LocalDate.now(clock);
    return new GraduationDisplayResult(
        invitationRepository.findPendingByInviteeUserId(requesterUserId).stream()
            .map(invitation -> GraduationDisplayResult.ExhibitionResult.from(invitation, today))
            .toList());
  }
}
