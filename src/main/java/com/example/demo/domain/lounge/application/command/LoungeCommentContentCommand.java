package com.example.demo.domain.lounge.application.command;

import java.util.List;

public record LoungeCommentContentCommand(String content, List<String> imageUrls) {

  public LoungeCommentContentCommand {
    imageUrls = imageUrls == null ? List.of() : List.copyOf(imageUrls);
  }

  public LoungeCommentContentCommand(String content) {
    this(content, List.of());
  }
}
