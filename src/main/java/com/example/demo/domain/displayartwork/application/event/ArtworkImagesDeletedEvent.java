package com.example.demo.domain.displayartwork.application.event;

import java.util.List;

public record ArtworkImagesDeletedEvent(List<String> imageUrls) {

  public ArtworkImagesDeletedEvent {
    imageUrls = imageUrls == null ? List.of() : List.copyOf(imageUrls);
  }
}
