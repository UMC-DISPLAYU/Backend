package com.example.demo.global.file.application.event;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public record MediaFilesRemovedEvent(List<String> fileUrls) {
  public MediaFilesRemovedEvent {
    fileUrls = List.copyOf(fileUrls);
  }

  public static MediaFilesRemovedEvent between(List<String> previous, List<String> retained) {
    Set<String> retainedUrls = new HashSet<>(retained);
    return new MediaFilesRemovedEvent(
        previous.stream().filter(url -> !retainedUrls.contains(url)).distinct().toList());
  }
}
