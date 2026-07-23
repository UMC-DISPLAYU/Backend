package com.example.demo.domain.display.presentation.request;

import com.example.demo.domain.display.application.command.DisplayLikeCommand;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record DisplayLikeRequest(@NotNull @Positive Long displayId) {

  public DisplayLikeCommand toCommand(Long userId) {
    return new DisplayLikeCommand(displayId, userId);
  }
}
