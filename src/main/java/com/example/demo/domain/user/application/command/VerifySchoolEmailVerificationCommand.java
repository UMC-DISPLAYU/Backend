package com.example.demo.domain.user.application.command;

public record VerifySchoolEmailVerificationCommand(
        String schoolEmail,
        String verificationCode
) {
}
