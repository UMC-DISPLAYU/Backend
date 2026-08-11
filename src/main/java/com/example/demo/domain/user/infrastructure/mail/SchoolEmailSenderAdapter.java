package com.example.demo.domain.user.infrastructure.mail;

import com.example.demo.domain.user.application.port.SchoolEmailSenderPort;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SchoolEmailSenderAdapter implements SchoolEmailSenderPort {

  private static final String SUBJECT = "[디스플레이유] 작가 인증 이메일 인증번호 안내";
  private static final String BODY_TEMPLATE =
      """
      <!DOCTYPE html>
      <html lang="ko">
        <body style="margin:0; padding:24px; font-family:Arial, sans-serif; color:#222;">
          <div style="max-width:560px; margin:0 auto;">
            <h2 style="margin-bottom:24px;">안녕하세요, 디스플레이유입니다.</h2>
            <p>작가 인증을 위한 학교 이메일 인증번호를 안내드립니다.</p>
            <div style="margin:24px 0; padding:20px; background:#f5f5f5; text-align:center; border-radius:8px;">
              <p style="margin:0 0 8px; font-size:14px;">인증번호</p>
              <strong style="font-size:32px; letter-spacing:6px;">%s</strong>
            </div>
            <p>인증번호는 발급 후 5분 동안 유효합니다.</p>
            <p style="color:#666;">본인이 요청하지 않았다면 이 메일을 무시해 주세요.</p>
          </div>
        </body>
      </html>
      """;

  private final JavaMailSender javaMailSender;

  @Override
  public void send(String schoolEmail, String verificationCode) {

    MimeMessage message = javaMailSender.createMimeMessage();

    try {
      MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

      helper.setTo(schoolEmail);
      helper.setSubject(SUBJECT);
      helper.setText(BODY_TEMPLATE.formatted(verificationCode), true);

      javaMailSender.send(message);

    } catch (MessagingException e) {
      throw new RuntimeException("메일 발송 실패", e);
    }
  }
}
