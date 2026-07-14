package com.example.demo.user.application.mapper;

import com.example.demo.user.application.command.AgreementCommand;
import com.example.demo.user.domain.entity.Agreement;
import com.example.demo.user.domain.entity.User;
import com.example.demo.user.domain.entity.UserAgreement;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class UserAgreementMapper {

  public UserAgreement toUserAgreement(User user, Agreement agreement, AgreementCommand command) {

    return UserAgreement.builder()
        .user(user)
        .agreement(agreement)
        .isAgreed(command.isAgreed())
        .agreedAt(LocalDateTime.now())
        .build();
  }
}
