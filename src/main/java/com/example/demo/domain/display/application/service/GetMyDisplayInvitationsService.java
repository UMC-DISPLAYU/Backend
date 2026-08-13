package com.example.demo.domain.display.application.service;

import com.example.demo.domain.display.application.result.MyDisplayInvitationListResult;
import com.example.demo.domain.display.domain.entity.DisplayInvitation;
import com.example.demo.domain.display.domain.repository.DisplayInvitationRepository;
import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.repository.UserRepository;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetMyDisplayInvitationsService {

  private final DisplayInvitationRepository invitationRepository;
  private final UserRepository userRepository;

  public GetMyDisplayInvitationsService(
      DisplayInvitationRepository invitationRepository, UserRepository userRepository) {
    this.invitationRepository = invitationRepository;
    this.userRepository = userRepository;
  }

  @Transactional(readOnly = true)
  public MyDisplayInvitationListResult getInvitations(Long requesterUserId) {
    List<DisplayInvitation> invitations =
        invitationRepository.findPendingByInviteeUserId(requesterUserId);
    Map<Long, User> invitersById =
        userRepository
            .findAllById(
                invitations.stream()
                    .map(invitation -> invitation.getInviterUserId().value())
                    .toList())
            .stream()
            .collect(Collectors.toMap(User::getId, Function.identity()));

    return MyDisplayInvitationListResult.from(invitations, invitersById);
  }
}
