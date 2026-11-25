package com.example.demo.domain.displayartwork.presentation.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ArtworkPartRequest(
    @Schema(example = "Blue Moment II") @NotBlank @Size(max = 255) String artworkName,
    @Schema(example = "2026") @Min(1000) Integer productionYear,
    @Schema(example = "Oil on canvas") @Size(max = 255) String materialMedia,
    @Schema(example = "45 x 60 cm") @Size(max = 255) String size) {}
