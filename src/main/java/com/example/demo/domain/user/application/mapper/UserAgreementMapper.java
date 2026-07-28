package com.example.demo.domain.user.application.mapper;

import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.entity.Agreement;
import com.example.demo.domain.user.domain.entity.UserAgreement;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class UserAgreementMapper {

  public UserAgreement toUserAgreement(User user, Agreement agreement) {

    return UserAgreement.builder()
        .user(user)
        .agreement(agreement)
        .isAgreed(true)
        .agreedAt(LocalDateTime.now())
        .build();
  }
}
