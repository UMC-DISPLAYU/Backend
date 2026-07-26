package com.example.demo.domain.display.presentation.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.List;

public record ReorderDisplayContentsRequest(
    @NotEmpty @Size(max = 20) List<@NotNull @Positive Long> orderedContentIds) {}
