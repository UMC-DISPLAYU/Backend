package com.example.demo.domain.display.infrastructure.persistence.adapter;

import com.example.demo.domain.display.application.port.DisplayContentPublicationPort;
import com.example.demo.domain.display.domain.type.ContentOpenPolicy;
import com.example.demo.domain.display.domain.type.DisplayContentStatus;
import com.example.demo.domain.display.domain.type.DisplayStatus;
import com.example.demo.domain.display.infrastructure.persistence.SpringDataDisplayContentJpaRepository;
import com.example.demo.domain.displayartwork.domain.type.DisplayArtworkStatus;
import com.example.demo.domain.displayartwork.infrastructure.persistence.SpringDataDisplayArtworkJpaRepository;
import java.time.LocalDate;
import org.springframework.stereotype.Repository;

@Repository
public class JpaDisplayContentPublicationAdapter implements DisplayContentPublicationPort {

  private final SpringDataDisplayContentJpaRepository displayContentJpaRepository;
  private final SpringDataDisplayArtworkJpaRepository displayArtworkJpaRepository;

  public JpaDisplayContentPublicationAdapter(
      SpringDataDisplayContentJpaRepository displayContentJpaRepository,
      SpringDataDisplayArtworkJpaRepository displayArtworkJpaRepository) {
    this.displayContentJpaRepository = displayContentJpaRepository;
    this.displayArtworkJpaRepository = displayArtworkJpaRepository;
  }

  @Override
  public int publishDisplayContentsOnExhibition(LocalDate today) {
    return displayContentJpaRepository.publishOnExhibition(
        today,
        DisplayStatus.PUBLISHED,
        ContentOpenPolicy.ON_EXHIBITION,
        DisplayContentStatus.DRAFT,
        DisplayContentStatus.PUBLISHED);
  }

  @Override
  public int publishDisplayArtworksOnExhibition(LocalDate today) {
    return displayArtworkJpaRepository.publishOnExhibition(
        today,
        DisplayStatus.PUBLISHED,
        ContentOpenPolicy.ON_EXHIBITION,
        DisplayArtworkStatus.DRAFT,
        DisplayArtworkStatus.PUBLISHED);
  }

  @Override
  public int publishDisplayContentsForDisplay(Long displayId, LocalDate today) {
    return displayContentJpaRepository.publishForDisplay(
        displayId,
        today,
        ContentOpenPolicy.ON_EXHIBITION,
        DisplayContentStatus.DRAFT,
        DisplayContentStatus.PUBLISHED);
  }

  @Override
  public int publishDisplayArtworksForDisplay(Long displayId, LocalDate today) {
    return displayArtworkJpaRepository.publishForDisplay(
        displayId,
        today,
        ContentOpenPolicy.ON_EXHIBITION,
        DisplayArtworkStatus.DRAFT,
        DisplayArtworkStatus.PUBLISHED);
  }
}
