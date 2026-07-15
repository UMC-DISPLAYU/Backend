package com.example.demo.domain.user.application.service;

import com.example.demo.domain.user.application.command.SendSchoolEmailVerificationCommand;
import com.example.demo.domain.user.domain.entity.SchoolEmailVerification;
import com.example.demo.domain.user.domain.repository.SchoolEmailVerificationRepository;
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

  @Transactional
  public void execute(SendSchoolEmailVerificationCommand command) {

    // 학교 이메일 + 도메인 검증
    schoolEmailValidator.validate(command.univName(), command.schoolEmail());

    // 기존 인증 정보 삭제
    verificationRepository.deleteBySchoolEmail(command.schoolEmail());

    // 새 인증번호 생성
    String verificationCode = createVerificationCode();

    // 새 인증 정보 저장
    SchoolEmailVerification verification =
        SchoolEmailVerification.create(command.schoolEmail(), command.univName(), verificationCode);

    verificationRepository.save(verification);

    // 이메일 발송
    emailSenderAdapter.send(command.schoolEmail(), verificationCode);
  }

  private String createVerificationCode() {

    return String.valueOf(new Random().nextInt(900000) + 100000);
  }
}
