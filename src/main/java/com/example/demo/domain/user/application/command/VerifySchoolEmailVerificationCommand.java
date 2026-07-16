package com.example.demo.domain.user.application.command;

public record VerifySchoolEmailVerificationCommand(
    Long userId, String schoolEmail, String verificationCode) {}
