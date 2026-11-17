package com.example.demo.domain.displayartwork.application.notification;

import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.entity.TeamMember;
import com.example.demo.domain.displayartwork.domain.repository.UserNicknameRepository;
import org.springframework.stereotype.Component;

/** 알림 문구에 넣을 사람 이름을 찾는다. 명세상 "등록한 사람·삭제한 사람"은 그 전시의 전시 작가명으로 표시한다. */
@Component
public class DisplayMemberNameResolver {

  private final UserNicknameRepository userNicknameRepository;

  public DisplayMemberNameResolver(UserNicknameRepository userNicknameRepository) {
    this.userNicknameRepository = userNicknameRepository;
  }

  /** 전시 작가명이 없으면(팀원이 아닌 경우) 서비스 닉네임으로 대신한다. */
  public String resolve(Display display, Long userId) {
    return display.getTeamMembers().stream()
        .filter(TeamMember::isAccepted)
        .filter(teamMember -> teamMember.getUserId().value().equals(userId))
        .map(TeamMember::getDisplayNickname)
        .findFirst()
        .or(() -> userNicknameRepository.findNicknameById(userId))
        .orElse("");
  }
}
