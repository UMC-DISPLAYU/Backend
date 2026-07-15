package com.example.demo.domain.user.presentation.response;

public record SchoolEmailVerificationConfirmResponse(
        String schoolEmail,
        boolean isVerified
) {}