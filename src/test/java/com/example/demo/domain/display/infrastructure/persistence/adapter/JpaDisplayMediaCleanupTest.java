package com.example.demo.domain.display.infrastructure.persistence.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.type.ContentOpenPolicy;
import com.example.demo.domain.display.domain.type.DisplayField;
import com.example.demo.domain.display.domain.type.DisplayType;
import com.example.demo.domain.display.domain.vo.DisplayLocation;
import com.example.demo.domain.display.domain.vo.DisplayPeriod;
import com.example.demo.domain.display.domain.vo.UserId;
import com.example.demo.domain.displayartwork.domain.aggregate.DisplayArtwork;
import com.example.demo.domain.displayartwork.domain.entity.ArtworkImage;
import com.example.demo.domain.displayartwork.domain.type.ArtworkImageType;
import com.example.demo.domain.displayartwork.domain.type.ArtworkType;
import com.example.demo.global.config.JpaAuditingConfig;
import com.example.demo.global.file.application.event.MediaFilesRemovedEvent;
import com.example.demo.global.file.domain.MediaContent;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@Import(JpaAuditingConfig.class)
class JpaDisplayMediaCleanupTest {
  @Autowired private EntityManager entityManager;
  private final ApplicationEventPublisher publisher = mock(ApplicationEventPublisher.class);

  @Test
  void deletingDisplayWithoutArtworksStillSchedulesItsOriginalAndThumbnail() {
    Display display = display();
    entityManager.persist(display);
    display.delete();
    entityManager.flush();
    new JpaDisplayDeletionCleanupAdapter(entityManager, publisher)
        .cleanupDisplayChildren(display.getId(), LocalDateTime.now());
    ArgumentCaptor<MediaFilesRemovedEvent> event =
        ArgumentCaptor.forClass(MediaFilesRemovedEvent.class);
    verify(publisher).publishEvent(event.capture());
    assertThat(event.getValue().fileUrls()).containsExactlyInAnyOrder("poster.mp4", "poster.png");
  }

  @Test
  void deletingDisplayAlsoSchedulesChildArtworkOriginalAndThumbnail() {
    Display display = display();
    entityManager.persist(display);
    DisplayArtwork artwork =
        DisplayArtwork.create(
            display,
            "작품",
            "내용",
            List.of(ArtworkType.MEDIA),
            2026,
            "영상",
            "크기",
            "감상",
            0,
            1L,
            List.of(
                new ArtworkImage(
                    null,
                    MediaContent.video("artwork.mp4", "artwork.png"),
                    true,
                    ArtworkImageType.ARTWORK,
                    0,
                    null,
                    800,
                    600)));
    entityManager.persist(artwork);
    display.delete();
    entityManager.flush();
    Long artworkId = artwork.getId();
    new JpaDisplayDeletionCleanupAdapter(entityManager, publisher)
        .cleanupDisplayChildren(display.getId(), LocalDateTime.now());
    ArgumentCaptor<MediaFilesRemovedEvent> event =
        ArgumentCaptor.forClass(MediaFilesRemovedEvent.class);
    verify(publisher).publishEvent(event.capture());
    assertThat(event.getValue().fileUrls())
        .containsExactlyInAnyOrder("poster.mp4", "poster.png", "artwork.mp4", "artwork.png");
    entityManager.clear();
    assertThat(entityManager.find(DisplayArtwork.class, artworkId).isDeleted()).isTrue();
  }

  private static Display display() {
    Display display =
        Display.create(
            new UserId(1L),
            "전시",
            "poster.png",
            "부제",
            "내용",
            new DisplayLocation("전시장", new BigDecimal("37.5"), new BigDecimal("127")),
            "",
            "",
            "학교",
            "학과",
            DisplayType.GRADUATION,
            List.of(DisplayField.DESIGN),
            new DisplayPeriod(
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 12, 31),
                LocalTime.of(10, 0),
                LocalTime.of(18, 0)),
            ContentOpenPolicy.IMMEDIATELY,
            ContentOpenPolicy.IMMEDIATELY);
    display.changeMedia(MediaContent.video("poster.mp4", "poster.png"), null, null);
    return display;
  }
}
