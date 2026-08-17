package com.example.demo.domain.user.application.service;

import com.example.demo.domain.user.application.command.SendSchoolEmailVerificationCommand;
import com.example.demo.domain.user.domain.entity.SchoolEmailVerification;
import com.example.demo.domain.user.domain.error.UserErrorCode;
import com.example.demo.domain.user.domain.error.UserException;
import com.example.demo.domain.user.domain.repository.SchoolEmailVerificationRepository;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ResendSchoolEmailVerificationService {

  private final SchoolEmailVerificationRepository verificationRepository;
  private final SendSchoolEmailVerificationService sendService;

  @Transactional
  public void execute(Long userId, String schoolEmail) {

    String normalizedSchoolEmail = normalize(schoolEmail);

    SchoolEmailVerification verification =
        verificationRepository
            .findByUserIdAndSchoolEmail(userId, normalizedSchoolEmail)
            .orElseThrow(() -> new UserException(UserErrorCode.EMAIL_VERIFICATION_NOT_FOUND));

    // 이미 인증 완료된 이메일
    if (verification.isVerified()) {
      throw new UserException(UserErrorCode.ALREADY_VERIFIED_USER);
    }

    sendService.execute(
        new SendSchoolEmailVerificationCommand(
            userId, normalizedSchoolEmail, verification.getUnivName()));
  }

  private String normalize(String schoolEmail) {
    if (schoolEmail == null || schoolEmail.isBlank()) {
      throw new UserException(UserErrorCode.INVALID_EMAIL);
    }
    return schoolEmail.trim().toLowerCase(Locale.ROOT);
  }
}
