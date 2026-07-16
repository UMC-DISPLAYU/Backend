package com.example.demo.domain.user.application.command;

public record SendSchoolEmailVerificationCommand(
        Long userId,
        String schoolEmail,
        String univName
) {}
