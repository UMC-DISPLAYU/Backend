package com.example.demo.domain.user.application.service;

import com.example.demo.domain.user.application.command.SendSchoolEmailVerificationCommand;
import com.example.demo.domain.user.domain.entity.SchoolEmailVerification;
import com.example.demo.domain.user.domain.repository.SchoolEmailVerificationRepository;
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
        verificationRepository.findBySchoolEmail(schoolEmail).orElseThrow();

    sendService.execute(
        new SendSchoolEmailVerificationCommand(schoolEmail, verification.getUnivName()));
  }
}
