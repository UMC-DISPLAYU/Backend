package com.example.demo.domain.display.application.mapper;

import com.example.demo.domain.display.application.result.DisplayMemberInvitationResult;
import com.example.demo.domain.display.domain.entity.DisplayInvitation;
import org.springframework.stereotype.Component;

@Component
public class DisplayMemberInvitationMapper {

  public DisplayMemberInvitationResult toResult(DisplayInvitation invitation) {
    return new DisplayMemberInvitationResult(
        invitation.getId(),
        invitation.getDisplay().getId(),
        invitation.getInviterUserId().value(),
        invitation.getInviteeUserId().value(),
        invitation.getStatus().name(),
        invitation.getCreatedAt(),
        invitation.getRespondedAt());
  }
}
