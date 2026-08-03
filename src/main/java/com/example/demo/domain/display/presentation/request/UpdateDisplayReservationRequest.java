package com.example.demo.domain.display.presentation.request;

import com.example.demo.domain.display.application.command.UpdateDisplayReservationCommand;
import com.example.demo.domain.display.domain.type.ContentOpenPolicy;
import jakarta.validation.constraints.NotNull;

public record UpdateDisplayReservationRequest(
    @NotNull ContentOpenPolicy artworkContentOpen,
    @NotNull ContentOpenPolicy exhibitionContentOpen) {

  public UpdateDisplayReservationCommand toCommand(Long userId, Long displayId) {
    return new UpdateDisplayReservationCommand(
        userId, displayId, artworkContentOpen, exhibitionContentOpen);
  }
}
