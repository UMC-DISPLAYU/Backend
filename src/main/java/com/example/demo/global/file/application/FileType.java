package com.example.demo.global.file.application;

import java.util.Set;

public enum FileType {
  IMAGE("images", Set.of("image/jpeg", "image/png", "image/webp", "image/gif", "image/heic")),
  VIDEO("videos", Set.of("video/mp4", "video/quicktime", "video/webm"));

  private final String directoryName;
  private final Set<String> contentTypes;

  FileType(String directoryName, Set<String> contentTypes) {
    this.directoryName = directoryName;
    this.contentTypes = contentTypes;
  }

  public String directoryName() {
    return directoryName;
  }

  public boolean supportsContentType(String contentType) {
    return contentType != null && contentTypes.contains(contentType);
  }
}
