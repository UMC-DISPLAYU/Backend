package com.example.demo.domain.display.presentation.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateDisplayContentCategoryRequest(@NotBlank String name, String description) {}
