package com.example.demo.domain.user.application.service;

import com.example.demo.domain.user.application.command.VerifySchoolEmailVerificationCommand;
import com.example.demo.domain.user.application.result.SchoolEmailConfirmVerificationResult;
import com.example.demo.domain.user.domain.entity.SchoolEmailVerification;
import com.example.demo.domain.user.domain.repository.SchoolEmailVerificationRepository;
import com.example.demo.domain.user.exception.UserErrorCode;
import com.example.demo.domain.user.exception.UserException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VerifySchoolEmailVerificationService {

  private final SchoolEmailVerificationRepository verificationRepository;

  @Transactional(noRollbackFor = UserException.class)
  public SchoolEmailConfirmVerificationResult execute(
      VerifySchoolEmailVerificationCommand command) {
    SchoolEmailVerification verification =
        verificationRepository
            .findByUserIdAndSchoolEmail(command.userId(), command.schoolEmail())
            .orElseThrow(() -> new UserException(UserErrorCode.EMAIL_VERIFICATION_NOT_FOUND));

    if (verification.isVerified()) {
      throw new UserException(UserErrorCode.ALREADY_VERIFIED_USER);
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
    verification.getUser().verifyAuthor(verification.getSchoolEmail(), verification.getUnivName());

    return new SchoolEmailConfirmVerificationResult(
        verification.getSchoolEmail(), verification.isVerified());
  }
}
