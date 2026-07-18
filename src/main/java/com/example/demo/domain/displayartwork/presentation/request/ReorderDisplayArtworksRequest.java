package com.example.demo.domain.displayartwork.presentation.request;

import com.example.demo.domain.displayartwork.application.command.ReorderDisplayArtworksCommand;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;

public record ReorderDisplayArtworksRequest(
    @NotNull @Positive Long displayId, @NotEmpty List<@NotNull Long> orderedArtworkIds) {

  public ReorderDisplayArtworksCommand toCommand() {
    return new ReorderDisplayArtworksCommand(displayId, orderedArtworkIds);
  }
}
