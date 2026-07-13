package com.example.demo.domain.user.application.command;

public record SendSchoolEmailVerificationCommand(
        String schoolEmail,
        String univName
) {
}
