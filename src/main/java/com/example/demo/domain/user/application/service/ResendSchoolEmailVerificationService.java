package com.example.demo.domain.user.application.service;

import com.example.demo.domain.user.application.command.SendSchoolEmailVerificationCommand;
import com.example.demo.domain.user.domain.entity.SchoolEmailVerification;
import com.example.demo.domain.user.domain.repository.SchoolEmailVerificationRepository;
import com.example.demo.domain.user.exception.UserErrorCode;
import com.example.demo.domain.user.exception.UserException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ResendSchoolEmailVerificationService {

  private final SchoolEmailVerificationRepository verificationRepository;
  private final SendSchoolEmailVerificationService sendService;

  @Transactional
  public void execute(String schoolEmail) {

    SchoolEmailVerification verification =
        verificationRepository
            .findBySchoolEmail(schoolEmail)
            .orElseThrow(() -> new UserException(UserErrorCode.EMAIL_VERIFICATION_NOT_FOUND));

    // 이미 인증 완료된 이메일
    if (verification.isVerified()) {
      throw new UserException(UserErrorCode.ALREADY_VERIFIED_USER);
    }

    // 재전송 쿨타임
    if (!verification.canResend()) {
      throw new UserException(UserErrorCode.EMAIL_SEND_COOLDOWN);
    }

    sendService.execute(
        new SendSchoolEmailVerificationCommand(schoolEmail, verification.getUnivName()));
  }
}
