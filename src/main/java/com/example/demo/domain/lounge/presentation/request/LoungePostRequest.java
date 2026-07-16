package com.example.demo.domain.lounge.presentation.request;

import com.example.demo.domain.lounge.application.command.LoungePostContentCommand;
import com.example.demo.domain.lounge.domain.type.LoungePostCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LoungePostRequest(
    @NotBlank @Size(max = 50) String title,
    @Size(max = 2048) String postImageUrl,
    @NotBlank @Size(max = 1000) String content,
    @NotNull LoungePostCategory category) {
  public LoungePostContentCommand toCommand() {
    return new LoungePostContentCommand(title, postImageUrl, content, category);
  }
}
