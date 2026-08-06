package com.example.demo.domain.display.application.service;

import com.example.demo.domain.display.application.port.DisplayContentPublicationPort;
import java.time.Clock;
import java.time.LocalDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DisplayContentPublicationService {

  private final DisplayContentPublicationPort publicationPort;
  private final Clock clock;

  public DisplayContentPublicationService(
      DisplayContentPublicationPort publicationPort, Clock clock) {
    this.publicationPort = publicationPort;
    this.clock = clock;
  }

  @Transactional
  public DisplayContentPublicationResult publishOnExhibitionContents() {
    LocalDate today = LocalDate.now(clock);
    return new DisplayContentPublicationResult(
        publicationPort.publishDisplayContentsOnExhibition(today),
        publicationPort.publishDisplayArtworksOnExhibition(today));
  }

  @Transactional
  public DisplayContentPublicationResult publishForDisplay(Long displayId) {
    LocalDate today = LocalDate.now(clock);
    return new DisplayContentPublicationResult(
        publicationPort.publishDisplayContentsForDisplay(displayId, today),
        publicationPort.publishDisplayArtworksForDisplay(displayId, today));
  }
}
