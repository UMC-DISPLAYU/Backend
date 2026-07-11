package com.example.demo.domain.lounge.presentation.request;

import com.example.demo.domain.lounge.application.command.LoungeCommentContentCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoungeCommentRequest(@NotBlank @Size(max = 300) String content) {

  public LoungeCommentContentCommand toCommand() {
    return new LoungeCommentContentCommand(content);
  }
}
