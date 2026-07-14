package com.example.demo.domain.user.application.command;

public record SchoolEmailVerificationConfirmCommand(
    Long userId, String schoolEmail, String verificationCode) {}
