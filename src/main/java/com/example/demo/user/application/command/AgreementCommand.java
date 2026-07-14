package com.example.demo.user.application.command;

public record AgreementCommand(
        Long agreeId,
        boolean isAgreed
) {
}
