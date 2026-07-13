package com.example.demo.domain.user.infrastructure.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SchoolEmailSenderAdapter {

  private final JavaMailSender javaMailSender;

  public void send(String schoolEmail, String verificationCode) {

    MimeMessage message = javaMailSender.createMimeMessage();

    try {
      MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

      helper.setTo(schoolEmail);
      helper.setSubject("작가 인증 이메일 인증번호");

      helper.setText("인증번호: " + verificationCode, false);

      javaMailSender.send(message);

    } catch (MessagingException e) {
      throw new RuntimeException("메일 발송 실패", e);
    }
  }
}
