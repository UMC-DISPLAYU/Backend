package com.example.demo.domain.lounge.presentation.request;

import com.example.demo.domain.lounge.application.command.LoungeCommentContentCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

public record LoungeCommentRequest(
    @NotBlank @Size(max = 300) String content,
    @Size(max = 5) List<@NotBlank @Size(max = 2048) String> imageUrls) {

  public LoungeCommentContentCommand toCommand() {
    return new LoungeCommentContentCommand(content, imageUrls);
  }
}
