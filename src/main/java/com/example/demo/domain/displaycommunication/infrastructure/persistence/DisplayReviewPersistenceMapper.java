package com.example.demo.domain.displaycommunication.infrastructure.persistence;

import com.example.demo.domain.displaycommunication.domain.repository.UserExistenceRepository.UserInfo;
import org.springframework.stereotype.Component;

@Component
public class DisplayReviewPersistenceMapper {

  private static final String WITHDRAWN_USER_NAME = "탈퇴한 회원";

  public UserInfo toUserInfo(DisplayReviewUserReferenceJpaEntity user) {
    if (user.getDeletedAt() != null) {
      return new UserInfo(user.getUserId(), WITHDRAWN_USER_NAME, null);
    }
    return new UserInfo(user.getUserId(), user.getNickname(), user.getProfileImageUrl());
  }
}
