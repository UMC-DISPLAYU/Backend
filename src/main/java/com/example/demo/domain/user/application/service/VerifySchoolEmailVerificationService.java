package com.example.demo.domain.user.application.service;

import com.example.demo.domain.user.application.command.VerifySchoolEmailVerificationCommand;
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

  @Transactional
  public SchoolEmailVerification execute(VerifySchoolEmailVerificationCommand command) {

    SchoolEmailVerification verification =
        verificationRepository
            .findBySchoolEmail(command.schoolEmail())
            .orElseThrow(() -> new UserException(UserErrorCode.EMAIL_VERIFICATION_NOT_FOUND));

    // 이미 인증 완료된 이메일
    if (verification.isVerified()) {

      throw new UserException(UserErrorCode.ALREADY_VERIFIED_USER);
    }

    // 인증번호 만료
    if (verification.isExpired()) {

      throw new UserException(UserErrorCode.VERIFICATION_CODE_EXPIRED);
    }

    // 인증번호 불일치
    if (!verification.matchCode(command.verificationCode())) {

      throw new UserException(UserErrorCode.VERIFICATION_CODE_MISMATCH);
    }

    // 인증 성공
    verification.verify();

    return verification;
  }
}
