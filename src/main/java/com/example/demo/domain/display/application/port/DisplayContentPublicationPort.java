package com.example.demo.domain.display.application.port;

import java.time.LocalDate;

public interface DisplayContentPublicationPort {

  int publishDisplayContentsOnExhibition(LocalDate today);

  int publishDisplayArtworksOnExhibition(LocalDate today);

  int publishDisplayContentsForDisplay(Long displayId, LocalDate today);

  int publishDisplayArtworksForDisplay(Long displayId, LocalDate today);
}
