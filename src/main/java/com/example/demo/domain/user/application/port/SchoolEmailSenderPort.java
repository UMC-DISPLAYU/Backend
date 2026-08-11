package com.example.demo.domain.user.application.port;

public interface SchoolEmailSenderPort {

  void send(String schoolEmail, String verificationCode);
}
