package com.example.demo.domain.display.application.service;

import com.example.demo.domain.display.application.command.UpdateMyDisplayNicknameCommand;
import com.example.demo.domain.display.application.result.DisplayMemberResult;
import com.example.demo.domain.display.domain.entity.TeamMember;
import com.example.demo.domain.display.domain.error.DisplayErrorCode;
import com.example.demo.domain.display.domain.repository.TeamMemberRepository;
import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.repository.UserRepository;
import com.example.demo.global.error.BusinessException;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateMyDisplayNicknameService {

  private final TeamMemberRepository teamMemberRepository;
  private final UserRepository userRepository;

  public UpdateMyDisplayNicknameService(
      TeamMemberRepository teamMemberRepository, UserRepository userRepository) {
    this.teamMemberRepository = teamMemberRepository;
    this.userRepository = userRepository;
  }

  @Transactional
  public DisplayMemberResult updateNickname(UpdateMyDisplayNicknameCommand command) {
    Objects.requireNonNull(command, "command must not be null.");

    TeamMember teamMember =
        teamMemberRepository
            .findAcceptedByDisplayIdAndUserId(command.displayId(), command.userId())
            .orElseThrow(() -> new BusinessException(DisplayErrorCode.DISPLAY_MEMBER_NOT_FOUND));

    teamMember.changeDisplayNickname(command.displayNickname());
    User user = userRepository.findById(command.userId()).orElse(null);
    return DisplayMemberResult.from(teamMember, user);
  }
}
