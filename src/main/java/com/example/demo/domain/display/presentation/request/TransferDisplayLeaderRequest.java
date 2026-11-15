package com.example.demo.domain.display.presentation.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record TransferDisplayLeaderRequest(@NotNull @Positive Long targetMemberId) {}
