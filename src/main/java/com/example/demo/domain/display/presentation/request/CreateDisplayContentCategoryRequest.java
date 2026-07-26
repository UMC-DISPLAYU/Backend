package com.example.demo.domain.display.presentation.request;

import jakarta.validation.constraints.NotBlank;

public record CreateDisplayContentCategoryRequest(
    @NotBlank String name, @NotBlank String description) {}
