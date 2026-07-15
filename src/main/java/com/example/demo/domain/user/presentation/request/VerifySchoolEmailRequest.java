package com.example.demo.domain.user.presentation.request;

public record VerifySchoolEmailRequest(String schoolEmail, String verificationCode) {}
