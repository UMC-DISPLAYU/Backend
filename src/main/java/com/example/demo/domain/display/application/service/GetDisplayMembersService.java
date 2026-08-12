package com.example.demo.domain.display.application.service;

import com.example.demo.domain.display.application.result.DisplayMemberListResult;
import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.entity.TeamMember;
import com.example.demo.domain.display.domain.error.DisplayErrorCode;
import com.example.demo.domain.display.domain.repository.DisplayRepository;
import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.repository.UserRepository;
import com.example.demo.global.error.BusinessException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetDisplayMembersService {

  private final DisplayRepository displayRepository;
  private final UserRepository userRepository;

  public GetDisplayMembersService(
      DisplayRepository displayRepository, UserRepository userRepository) {
    this.displayRepository = displayRepository;
    this.userRepository = userRepository;
  }

  @Transactional(readOnly = true)
  public DisplayMemberListResult getMembers(Long displayId) {
    Display display =
        displayRepository
            .findById(displayId)
            .filter(candidate -> !candidate.isDeleted())
            .orElseThrow(() -> new BusinessException(DisplayErrorCode.DISPLAY_NOT_FOUND));

    List<TeamMember> teamMembers =
        display.getTeamMembers().stream().filter(teamMember -> !teamMember.isDeleted()).toList();
    Map<Long, User> usersById =
        userRepository
            .findAllById(teamMembers.stream().map(member -> member.getUserId().value()).toList())
            .stream()
            .collect(Collectors.toMap(User::getId, Function.identity()));

    return new DisplayMemberListResult(
        display.getId(),
        teamMembers.stream()
            .map(teamMember -> toResult(teamMember, usersById.get(teamMember.getUserId().value())))
            .toList());
  }

  private DisplayMemberListResult.TeamMemberResult toResult(TeamMember teamMember, User user) {
    return new DisplayMemberListResult.TeamMemberResult(
        teamMember.getId(),
        teamMember.getUserId().value(),
        teamMember.getDisplayNickname(),
        user != null && user.getDeletedAt() == null,
        user != null && user.isVerified(),
        teamMember.isAccepted(),
        teamMember.getRole().name());
  }
}
