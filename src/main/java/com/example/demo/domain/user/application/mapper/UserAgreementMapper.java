package com.example.demo.domain.user.application.mapper;

import com.example.demo.domain.user.application.command.AgreementCommand;
import com.example.demo.domain.user.domain.entity.Agreement;
import com.example.demo.domain.user.domain.entity.User;
import com.example.demo.domain.user.domain.entity.UserAgreement;
import java.time.LocalDateTime;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Mapper
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
