package com.example.demo.domain.admin.presentation.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RejectReviewRequest(@NotBlank @Size(max = 1000) String reason) {}
