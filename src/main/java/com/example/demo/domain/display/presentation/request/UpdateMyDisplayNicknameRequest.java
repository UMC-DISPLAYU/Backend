package com.example.demo.domain.display.presentation.request;

import com.example.demo.domain.display.application.command.UpdateMyDisplayNicknameCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record UpdateMyDisplayNicknameRequest(
    @NotNull @Positive Long displayId, @NotBlank @Size(max = 255) String displayNickname) {

  public UpdateMyDisplayNicknameCommand toCommand(Long userId) {
    return new UpdateMyDisplayNicknameCommand(userId, displayId, displayNickname);
  }
}
