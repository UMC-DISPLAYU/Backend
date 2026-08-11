package com.example.demo.domain.displayartwork.presentation.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;

public record ReorderDisplayArtworksRequest(
    @NotNull @Positive Long displayId, @NotEmpty List<@NotNull Long> orderedArtworkIds) {}
