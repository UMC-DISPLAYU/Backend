package com.example.demo.domain.lounge.presentation.request;

import com.example.demo.domain.lounge.application.command.LoungePostContentCommand;
import com.example.demo.domain.lounge.domain.type.LoungePostCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoungePostRequest(
    @NotBlank String title,
    String postImageUrl,
    @NotBlank String content,
    @NotNull LoungePostCategory category) {
  public LoungePostContentCommand toCommand() {
    return new LoungePostContentCommand(title, postImageUrl, content, category);
  }
}
