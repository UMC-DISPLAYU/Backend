package com.example.demo.domain.user.infrastructure.mail;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;
import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSender;

class SchoolEmailSenderAdapterTest {

  @Test
  void sendsVerificationCodeWithGuidanceAsHtml() throws Exception {
    JavaMailSender javaMailSender = mock(JavaMailSender.class);
    MimeMessage message = new MimeMessage(Session.getInstance(new Properties()));
    when(javaMailSender.createMimeMessage()).thenReturn(message);
    SchoolEmailSenderAdapter adapter = new SchoolEmailSenderAdapter(javaMailSender);

    adapter.send("artist@university.ac.kr", "123456");
    message.saveChanges();

    assertThat(message.getRecipients(Message.RecipientType.TO)[0].toString())
        .isEqualTo("artist@university.ac.kr");
    assertThat(message.getSubject()).isEqualTo("[디스플레이유] 작가 인증 이메일 인증번호 안내");
    assertThat(message.getContentType()).startsWith("text/html");
    assertThat(message.getContent().toString())
        .contains(
            "안녕하세요, 디스플레이유입니다.",
            "작가 인증을 위한 학교 이메일 인증번호를 안내드립니다.",
            "123456",
            "발급 후 5분 동안 유효합니다.",
            "본인이 요청하지 않았다면 이 메일을 무시해 주세요.");
    verify(javaMailSender).send(message);
  }
}
