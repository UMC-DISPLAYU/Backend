package com.example.demo.domain.display.application.service;

import com.example.demo.domain.display.application.result.MyDisplayInvitationListResult;
import com.example.demo.domain.display.domain.repository.DisplayInvitationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetMyDisplayInvitationsService {

  private final DisplayInvitationRepository invitationRepository;

  public GetMyDisplayInvitationsService(DisplayInvitationRepository invitationRepository) {
    this.invitationRepository = invitationRepository;
  }

  @Transactional(readOnly = true)
  public MyDisplayInvitationListResult getInvitations(Long requesterUserId) {
    return MyDisplayInvitationListResult.from(
        invitationRepository.findPendingByInviteeUserId(requesterUserId));
  }
}
