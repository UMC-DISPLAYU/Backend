package com.example.demo.domain.display.application.port;

public interface ArtworkCreatorRenamePort {

  int rename(Long displayId, Long userId, String previousName, String newName);
}
