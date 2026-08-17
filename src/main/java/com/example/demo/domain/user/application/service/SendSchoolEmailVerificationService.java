package com.example.demo.domain.user.application.service;

import com.example.demo.domain.user.application.command.SendSchoolEmailVerificationCommand;
import com.example.demo.domain.user.application.port.SchoolEmailSenderPort;
import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.entity.SchoolEmailVerification;
import com.example.demo.domain.user.domain.error.UserErrorCode;
import com.example.demo.domain.user.domain.error.UserException;
import com.example.demo.domain.user.domain.repository.SchoolEmailVerificationRepository;
import com.example.demo.domain.user.domain.repository.UserRepository;
import com.example.demo.domain.user.domain.service.SchoolEmailValidator;
import java.security.SecureRandom;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SendSchoolEmailVerificationService {

  private static final SecureRandom SECURE_RANDOM = new SecureRandom();

  private final SchoolEmailVerificationRepository verificationRepository;
  private final SchoolEmailSenderPort emailSender;
  private final SchoolEmailValidator schoolEmailValidator;
  private final UserRepository userRepository;

  @Transactional
  public void execute(SendSchoolEmailVerificationCommand command) {

    String schoolEmail = normalize(command.schoolEmail());

    // 학교 이메일 + 도메인 검증
    schoolEmailValidator.validate(command.univName(), schoolEmail);

    User user =
        userRepository
            .findById(command.userId())
            .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

    if (user.getSchoolEmail() != null) {
      throw new UserException(UserErrorCode.ALREADY_VERIFIED_USER);
    }
    if (userRepository.existsBySchoolEmail(schoolEmail)) {
      throw new UserException(UserErrorCode.DUPLICATE_SCHOOL_EMAIL);
    }

    verificationRepository
        .findByUserIdAndSchoolEmail(command.userId(), schoolEmail)
        .ifPresent(
            verification -> {
              if (!verification.canResend()) {
                throw new UserException(UserErrorCode.EMAIL_SEND_COOLDOWN);
              }
            });

    // 기존 인증 정보 삭제
    verificationRepository.deleteByUserIdAndSchoolEmail(command.userId(), schoolEmail);

    // 새 인증번호 생성
    String verificationCode = createVerificationCode();

    // 새 인증 정보 저장
    SchoolEmailVerification verification =
        SchoolEmailVerification.create(user, schoolEmail, command.univName(), verificationCode);

    verificationRepository.save(verification);

    // 이메일 발송
    emailSender.send(schoolEmail, verificationCode);
  }

  private String normalize(String schoolEmail) {
    return schoolEmail.trim().toLowerCase(Locale.ROOT);
  }

  private String createVerificationCode() {

    return String.valueOf(SECURE_RANDOM.nextInt(900000) + 100000);
  }
}
