package com.example.demo.domain.displaycommunication.infrastructure.persistence;

import com.example.demo.domain.displaycommunication.domain.repository.DisplayReviewAccessRepository.DisplayReviewAccess;
import com.example.demo.domain.displaycommunication.domain.repository.UserExistenceRepository.UserInfo;
import org.springframework.stereotype.Component;

@Component
public class DisplayReviewPersistenceMapper {

  private static final String PUBLISHED = "PUBLISHED";

  public DisplayReviewAccess toAccess(
      DisplayReferenceJpaEntity display, boolean acceptedTeamMember) {
    return new DisplayReviewAccess(
        display.getOwnerUserId(),
        display.getStartDate(),
        display.getEndDate(),
        PUBLISHED.equals(display.getStatus()),
        acceptedTeamMember);
  }

  public UserInfo toUserInfo(DisplayReviewUserReferenceJpaEntity user) {
    return new UserInfo(user.getUserId(), user.getNickname(), user.getProfileImageUrl());
  }
}
