package com.example.demo.domain.displayartwork.application.port;

import java.util.List;

public interface ArtworkImageDeletionPort {

  void deleteAll(List<String> imageUrls);
}
