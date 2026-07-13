package com.example.demo.domain.user.presentation.request;

public record SchoolEmailVerificationRequest(
        String schoolEmail,
        String univName
) {
}