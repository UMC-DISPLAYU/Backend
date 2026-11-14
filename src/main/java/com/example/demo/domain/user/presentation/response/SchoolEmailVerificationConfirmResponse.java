package com.example.demo.domain.user.presentation.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record SchoolEmailVerificationConfirmResponse(
    @Schema(example = "diyu@dongguk.edu") String schoolEmail,
    @Schema(example = "true") boolean isVerified) {}
