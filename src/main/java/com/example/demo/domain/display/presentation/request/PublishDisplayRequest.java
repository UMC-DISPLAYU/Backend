package com.example.demo.domain.display.presentation.request;

import com.example.demo.domain.display.application.command.PublishDisplayCommand;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PublishDisplayRequest(@NotNull @Positive Long displayId) {

  public PublishDisplayCommand toCommand(Long userId) {
    return new PublishDisplayCommand(userId, displayId);
  }
}
