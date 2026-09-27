package com.example.demo.domain.user.application.service;

import static com.example.demo.global.util.StringNormalizer.normalize;

import com.example.demo.domain.user.application.permission.UserPermissionChecker;
import com.example.demo.domain.user.contract.command.ChangeUserUniversityCommand;
import com.example.demo.domain.user.contract.command.ChangeUserUniversityResult;
import com.example.demo.domain.user.contract.command.ChangeUserUniversityUseCase;
import com.example.demo.domain.user.contract.event.v1.UserUniversityChangedEvent;
import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.error.UserErrorCode;
import com.example.demo.domain.user.domain.error.UserException;
import com.example.demo.domain.user.domain.repository.UserRepository;
import com.example.demo.domain.user.domain.service.SchoolEmailValidator;
import java.time.Clock;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChangeUserUniversityService implements ChangeUserUniversityUseCase {

  private final UserRepository userRepository;
  private final SchoolEmailValidator schoolEmailValidator;
  private final UserPermissionChecker permissionChecker;
  private final ApplicationEventPublisher eventPublisher;
  private final Clock clock;

  @Override
  @Transactional
  public ChangeUserUniversityResult execute(ChangeUserUniversityCommand command) {
    User user =
        userRepository
            .findById(command.userId())
            .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
    permissionChecker.requireActive(user);

    String universityName = normalize(command.universityName());
    schoolEmailValidator.validate(universityName, user.getSchoolEmail());
    user.changeUnivName(universityName);
    eventPublisher.publishEvent(
        new UserUniversityChangedEvent(
            UUID.randomUUID(),
            user.getId(),
            universityName,
            user.getQualificationRevision(),
            clock.instant()));

    return new ChangeUserUniversityResult(universityName);
  }
}
