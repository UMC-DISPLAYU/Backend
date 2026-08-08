package com.example.demo.domain.display.presentation.request;

import com.example.demo.domain.display.application.command.HideDisplayCommand;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record HideDisplayRequest(@NotNull @Positive Long displayId) {

  public HideDisplayCommand toCommand(Long userId) {
    return new HideDisplayCommand(userId, displayId);
  }
}
