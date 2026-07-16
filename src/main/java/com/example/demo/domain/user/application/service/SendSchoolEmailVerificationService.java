package com.example.demo.domain.user.application.service;

import com.example.demo.domain.user.application.command.SendSchoolEmailVerificationCommand;
import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.entity.SchoolEmailVerification;
import com.example.demo.domain.user.domain.repository.SchoolEmailVerificationRepository;
import com.example.demo.domain.user.domain.repository.UserRepository;
import com.example.demo.domain.user.exception.UserErrorCode;
import com.example.demo.domain.user.exception.UserException;
import com.example.demo.domain.user.infrastructure.mail.SchoolEmailSenderAdapter;
import com.example.demo.domain.user.validator.SchoolEmailValidator;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SendSchoolEmailVerificationService {

  private final SchoolEmailVerificationRepository verificationRepository;
  private final SchoolEmailSenderAdapter emailSenderAdapter;
  private final SchoolEmailValidator schoolEmailValidator;
  private final UserRepository userRepository;

  @Transactional
  public void execute(SendSchoolEmailVerificationCommand command) {
    schoolEmailValidator.validate(command.univName(), command.schoolEmail());

    User user =
        userRepository
            .findById(command.userId())
            .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

    verificationRepository.deleteByUserIdAndSchoolEmail(command.userId(), command.schoolEmail());

    String verificationCode = createVerificationCode();
    SchoolEmailVerification verification =
        SchoolEmailVerification.create(
            user, command.schoolEmail(), command.univName(), verificationCode);

    verificationRepository.save(verification);
    emailSenderAdapter.send(command.schoolEmail(), verificationCode);
  }

  private String createVerificationCode() {
    return String.valueOf(new Random().nextInt(900000) + 100000);
  }
}
