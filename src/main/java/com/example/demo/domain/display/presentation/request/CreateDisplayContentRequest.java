package com.example.demo.domain.display.presentation.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record CreateDisplayContentRequest(
    @NotBlank String imageUrl, @Positive int width, @Positive int height) {}
