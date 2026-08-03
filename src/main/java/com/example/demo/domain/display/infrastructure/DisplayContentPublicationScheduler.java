package com.example.demo.domain.display.infrastructure;

import com.example.demo.domain.display.application.service.DisplayContentPublicationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DisplayContentPublicationScheduler {

  private final DisplayContentPublicationService publicationService;

  public DisplayContentPublicationScheduler(DisplayContentPublicationService publicationService) {
    this.publicationService = publicationService;
  }

  @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
  public void publishOnExhibitionContents() {
    publicationService.publishOnExhibitionContents();
  }
}
