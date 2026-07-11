package com.example.demo.domain.lounge.presentation.request;

import com.example.demo.domain.lounge.application.command.LoungeCommentContentCommand;
import jakarta.validation.constraints.NotBlank;

public record LoungeCommentRequest(@NotBlank String content) {

  public LoungeCommentContentCommand toCommand() {
    return new LoungeCommentContentCommand(content);
  }
}
