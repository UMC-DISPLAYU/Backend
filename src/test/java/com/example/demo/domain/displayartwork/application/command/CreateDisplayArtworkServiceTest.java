package com.example.demo.domain.displayartwork.application.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.repository.DisplayRepository;
import com.example.demo.domain.display.domain.type.ContentOpenPolicy;
import com.example.demo.domain.display.domain.type.DisplayField;
import com.example.demo.domain.display.domain.type.DisplayRegion;
import com.example.demo.domain.display.domain.type.DisplayType;
import com.example.demo.domain.display.domain.vo.DisplayLocation;
import com.example.demo.domain.display.domain.vo.DisplayPeriod;
import com.example.demo.domain.display.domain.vo.UserId;
import com.example.demo.domain.displayartwork.application.result.AuthorSetupResult;
import com.example.demo.domain.displayartwork.domain.aggregate.DisplayArtwork;
import com.example.demo.domain.displayartwork.domain.repository.ArtistVerificationRepository;
import com.example.demo.domain.displayartwork.domain.repository.DisplayArtworkRepository;
import com.example.demo.domain.displayartwork.domain.type.ArtworkImageType;
import com.example.demo.domain.displayartwork.domain.type.ArtworkType;
import com.example.demo.domain.displayartwork.domain.type.DisplayArtworkStatus;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class CreateDisplayArtworkServiceTest {

  private final DisplayRepository displayRepository = mock(DisplayRepository.class);
  private final DisplayArtworkRepository displayArtworkRepository =
      mock(DisplayArtworkRepository.class);
  private final ArtistVerificationRepository artistVerificationRepository =
      mock(ArtistVerificationRepository.class);
  private final AuthorSetupService authorSetupService = mock(AuthorSetupService.class);
  private final Clock clock =
      Clock.fixed(Instant.parse("2026-08-01T15:00:00Z"), ZoneId.of("Asia/Seoul"));
  private final CreateDisplayArtworkService service =
      new CreateDisplayArtworkService(
          displayRepository,
          displayArtworkRepository,
          artistVerificationRepository,
          authorSetupService,
          clock);

  @Test
  void createDisplayArtworkCreatesDraftWhenDisplayIsDraftEvenIfImmediateOpen() {
    Display display = display(ContentOpenPolicy.IMMEDIATELY);
    when(displayRepository.findById(1L)).thenReturn(Optional.of(display));
    when(displayArtworkRepository.countByDisplayId(1L)).thenReturn(0);
    when(artistVerificationRepository.isVerifiedArtist(1L)).thenReturn(true);
    when(displayArtworkRepository.save(any(DisplayArtwork.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    when(authorSetupService.setup(any(Long.class), any(AuthorSetupCommand.class)))
        .thenReturn(new AuthorSetupResult(null, "작가", 1L, 0, List.of(1L)));
    ArgumentCaptor<DisplayArtwork> artworkCaptor = ArgumentCaptor.forClass(DisplayArtwork.class);

    service.createDisplayArtwork(1L, command());

    verify(displayArtworkRepository).save(artworkCaptor.capture());
    assertThat(artworkCaptor.getValue().getStatus()).isEqualTo(DisplayArtworkStatus.DRAFT);
  }

  private static Display display(ContentOpenPolicy artworkContentOpen) {
    return Display.create(
        new UserId(1L),
        "FORM 2026",
        "https://cdn.displayu.com/posters/main.png",
        "subtitle",
        "content",
        new DisplayLocation("전시장", bd("37.5513"), bd("126.9248")),
        "",
        "",
        "organization",
        "department",
        DisplayType.GRADUATION,
        List.of(DisplayField.DESIGN),
        DisplayRegion.SEOUL,
        new DisplayPeriod(
            LocalDate.of(2026, 5, 28),
            LocalDate.of(2026, 6, 5),
            LocalTime.of(10, 0),
            LocalTime.of(18, 0)),
        artworkContentOpen,
        ContentOpenPolicy.ON_EXHIBITION);
  }

  private static CreateDisplayArtworkCommand command() {
    return new CreateDisplayArtworkCommand(
        1L,
        "작품명",
        "작품 설명입니다.",
        ArtworkType.DESIGN,
        2026,
        "디지털 프린트",
        "100x100cm",
        "작품 포인트입니다.",
        List.of(
            new ArtworkImageCommand(
                "https://cdn.displayu.com/artwork/main.jpg",
                true,
                ArtworkImageType.ARTWORK,
                0,
                "대표 이미지",
                1200,
                800)),
        "작가",
        1L,
        List.of(),
        List.of(),
        List.of(1L));
  }

  private static BigDecimal bd(String value) {
    return new BigDecimal(value);
  }
}
