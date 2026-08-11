package com.example.demo.domain.displaycommunication.infrastructure.persistence;

import com.example.demo.domain.displaycommunication.domain.repository.UserExistenceRepository.UserInfo;
import org.springframework.stereotype.Component;

@Component
public class DisplayReviewPersistenceMapper {

  public UserInfo toUserInfo(DisplayReviewUserReferenceJpaEntity user) {
    return new UserInfo(user.getUserId(), user.getNickname(), user.getProfileImageUrl());
  }
}
