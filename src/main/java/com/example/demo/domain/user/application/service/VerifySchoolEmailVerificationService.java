package com.example.demo.domain.user.application.service;

import com.example.demo.domain.user.application.command.VerifySchoolEmailVerificationCommand;
import com.example.demo.domain.user.application.result.SchoolEmailConfirmVerificationResult;
import com.example.demo.domain.user.domain.entity.SchoolEmailVerification;
import com.example.demo.domain.user.domain.error.UserErrorCode;
import com.example.demo.domain.user.domain.error.UserException;
import com.example.demo.domain.user.domain.repository.SchoolEmailVerificationRepository;
import com.example.demo.domain.user.domain.repository.UserRepository;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VerifySchoolEmailVerificationService {

  private final SchoolEmailVerificationRepository verificationRepository;
  private final UserRepository userRepository;

  @Transactional(noRollbackFor = UserException.class)
  public SchoolEmailConfirmVerificationResult execute(
      VerifySchoolEmailVerificationCommand command) {
    String schoolEmail = normalize(command.schoolEmail());
    SchoolEmailVerification verification =
        verificationRepository
            .findByUserIdAndSchoolEmail(command.userId(), schoolEmail)
            .orElseThrow(() -> new UserException(UserErrorCode.EMAIL_VERIFICATION_NOT_FOUND));

    if (verification.isVerified()) {
      throw new UserException(UserErrorCode.ALREADY_VERIFIED_USER);
    }
    if (verification.getUser().getSchoolEmail() != null) {
      throw new UserException(UserErrorCode.ALREADY_VERIFIED_USER);
    }
    if (userRepository.existsBySchoolEmail(schoolEmail)) {
      throw new UserException(UserErrorCode.DUPLICATE_SCHOOL_EMAIL);
    }
    if (verification.hasExceededMaxAttempts()) {
      throw new UserException(UserErrorCode.VERIFICATION_ATTEMPTS_EXCEEDED);
    }

    if (verification.isExpired()) {
      throw new UserException(UserErrorCode.VERIFICATION_CODE_EXPIRED);
    }
    if (!verification.matchCode(command.verificationCode())) {
      verification.recordFailedAttempt();
      verificationRepository.save(verification);

      if (verification.hasExceededMaxAttempts()) {
        throw new UserException(UserErrorCode.VERIFICATION_ATTEMPTS_EXCEEDED);
      }

      throw new UserException(UserErrorCode.VERIFICATION_CODE_MISMATCH);
    }

    verification.verify();
    verification
        .getUser()
        .verifySchoolEmail(verification.getSchoolEmail(), verification.getUnivName());

    return new SchoolEmailConfirmVerificationResult(
        verification.getSchoolEmail(), verification.isVerified());
  }

  private String normalize(String schoolEmail) {
    return schoolEmail.trim().toLowerCase(Locale.ROOT);
  }
}
