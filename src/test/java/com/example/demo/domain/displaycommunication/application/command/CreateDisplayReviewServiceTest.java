package com.example.demo.domain.displaycommunication.application.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.domain.display.application.result.DisplayReviewAccessResult;
import com.example.demo.domain.display.application.usecase.GetDisplayReviewAccessUseCase;
import com.example.demo.domain.display.domain.error.DisplayErrorCode;
import com.example.demo.domain.displaycommunication.application.result.DisplayReviewResult;
import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReview;
import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReview.ImageInfo;
import com.example.demo.domain.displaycommunication.domain.error.DisplayCommunicationErrorCode;
import com.example.demo.domain.displaycommunication.domain.repository.DisplayReviewReplyRepository;
import com.example.demo.domain.displaycommunication.domain.repository.DisplayReviewRepository;
import com.example.demo.domain.displaycommunication.domain.repository.UserExistenceRepository;
import com.example.demo.global.error.BusinessException;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateDisplayReviewServiceTest {
  @Mock private GetDisplayReviewAccessUseCase getDisplayReviewAccessUseCase;
  @Mock private DisplayReviewRepository displayReviewRepository;
  @Mock private DisplayReviewReplyRepository displayReviewReplyRepository;
  @Mock private UserExistenceRepository userExistenceRepository;

  private CreateDisplayReviewService service;
  private DisplayReviewAccessResult ongoingAccess;

  @BeforeEach
  void setUp() {
    ongoingAccess =
        new DisplayReviewAccessResult(
            1L, LocalDate.of(2026, 7, 22), LocalDate.of(2026, 7, 24), true, Set.of());
    lenient().when(userExistenceRepository.existsById(2L)).thenReturn(true);
    lenient()
        .when(getDisplayReviewAccessUseCase.getDisplayReviewAccess(1L))
        .thenReturn(Optional.of(ongoingAccess));
    Clock clock = Clock.fixed(Instant.parse("2026-07-23T00:00:00Z"), ZoneId.of("Asia/Seoul"));
    DisplayReviewValidator validator =
        new DisplayReviewValidator(
            getDisplayReviewAccessUseCase,
            displayReviewRepository,
            displayReviewReplyRepository,
            userExistenceRepository,
            clock);
    service = new CreateDisplayReviewService(validator, displayReviewRepository);
  }

  @Test
  void memberCreatesReviewWithImages() {
    when(displayReviewRepository.save(any(DisplayReview.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    DisplayReviewResult result =
        service.create(
            new CreateDisplayReviewCommand(
                1L,
                2L,
                "작품과 전시 주제가 잘 연결되어 있어요.",
                List.of(new ImageInfo("https://cdn.test/review.jpg", 1200, 800))));

    assertThat(result.content()).isEqualTo("작품과 전시 주제가 잘 연결되어 있어요.");
    assertThat(result.displayId()).isEqualTo(1L);
    assertThat(result.userId()).isEqualTo(2L);
    assertThat(result.images()).hasSize(1);
    verify(displayReviewRepository).save(any(DisplayReview.class));
  }

  @Test
  void createFailsWhenDisplayDoesNotExist() {
    when(getDisplayReviewAccessUseCase.getDisplayReviewAccess(99L)).thenReturn(Optional.empty());

    assertThatThrownBy(
            () -> service.create(new CreateDisplayReviewCommand(99L, 2L, "후기", List.of())))
        .isInstanceOfSatisfying(
            BusinessException.class,
            exception ->
                assertThat(exception.errorCode()).isEqualTo(DisplayErrorCode.DISPLAY_NOT_FOUND));

    verify(displayReviewRepository, never()).save(any());
  }

  @Test
  void reviewCannotBeCreatedBeforeDisplayStarts() {
    mockAccess(
        new DisplayReviewAccessResult(
            1L, LocalDate.of(2026, 7, 24), LocalDate.of(2026, 7, 30), true, Set.of()));

    assertBusinessError(
        new CreateDisplayReviewCommand(1L, 2L, "후기", List.of()),
        DisplayCommunicationErrorCode.DISPLAY_REVIEW_NOT_WRITABLE);
  }

  @Test
  void reviewCanBeCreatedOnDisplayStartDate() {
    mockAccess(
        new DisplayReviewAccessResult(
            1L, LocalDate.of(2026, 7, 23), LocalDate.of(2026, 7, 30), true, Set.of()));
    when(displayReviewRepository.save(any(DisplayReview.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    DisplayReviewResult result =
        service.create(new CreateDisplayReviewCommand(1L, 2L, "시작일 후기", List.of()));

    assertThat(result.content()).isEqualTo("시작일 후기");
    verify(displayReviewRepository).save(any(DisplayReview.class));
  }

  @Test
  void reviewCanBeCreatedAfterDisplayEnds() {
    mockAccess(
        new DisplayReviewAccessResult(
            1L, LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 22), true, Set.of()));
    when(displayReviewRepository.save(any(DisplayReview.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    DisplayReviewResult result =
        service.create(new CreateDisplayReviewCommand(1L, 2L, "종료된 전시 후기", List.of()));

    assertThat(result.content()).isEqualTo("종료된 전시 후기");
    verify(displayReviewRepository).save(any(DisplayReview.class));
  }

  @Test
  void reviewCannotBeCreatedForUnpublishedDisplay() {
    mockAccess(
        new DisplayReviewAccessResult(
            1L, LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 30), false, Set.of()));

    assertBusinessError(
        new CreateDisplayReviewCommand(1L, 2L, "후기", List.of()),
        DisplayCommunicationErrorCode.DISPLAY_REVIEW_NOT_WRITABLE);
  }

  @Test
  void memberCanCreateMultipleReviewsForSameDisplay() {
    when(displayReviewRepository.save(any(DisplayReview.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    service.create(new CreateDisplayReviewCommand(1L, 2L, "첫 번째 후기", List.of()));
    service.create(new CreateDisplayReviewCommand(1L, 2L, "두 번째 후기", List.of()));

    verify(displayReviewRepository, times(2)).save(any(DisplayReview.class));
  }

  @Test
  void blankReviewContentIsRejectedByValidator() {
    assertBusinessError(
        new CreateDisplayReviewCommand(1L, 2L, " ", List.of()),
        DisplayCommunicationErrorCode.INVALID_DISPLAY_REVIEW_CONTENT);
  }

  @Test
  void moreThanFiveImagesAreRejectedByValidator() {
    List<ImageInfo> images =
        List.of(
            image("1.jpg"),
            image("2.jpg"),
            image("3.jpg"),
            image("4.jpg"),
            image("5.jpg"),
            image("6.jpg"));

    assertBusinessError(
        new CreateDisplayReviewCommand(1L, 2L, "후기", images),
        DisplayCommunicationErrorCode.INVALID_DISPLAY_REVIEW_IMAGES);
  }

  @Test
  void fiveImagesAreAllowed() {
    List<ImageInfo> images =
        List.of(image("1.jpg"), image("2.jpg"), image("3.jpg"), image("4.jpg"), image("5.jpg"));
    when(displayReviewRepository.save(any(DisplayReview.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    DisplayReviewResult result =
        service.create(new CreateDisplayReviewCommand(1L, 2L, "후기", images));

    assertThat(result.images()).hasSize(5);
    verify(displayReviewRepository).save(any(DisplayReview.class));
  }

  @Test
  void missingOrWithdrawnUserCannotCreateReview() {
    when(userExistenceRepository.existsById(2L)).thenReturn(false);

    assertBusinessError(
        new CreateDisplayReviewCommand(1L, 2L, "후기", List.of()),
        DisplayCommunicationErrorCode.USER_NOT_FOUND);
  }

  private void mockAccess(DisplayReviewAccessResult access) {
    when(getDisplayReviewAccessUseCase.getDisplayReviewAccess(1L)).thenReturn(Optional.of(access));
  }

  private void assertBusinessError(
      CreateDisplayReviewCommand command, DisplayCommunicationErrorCode errorCode) {
    assertThatThrownBy(() -> service.create(command))
        .isInstanceOfSatisfying(
            BusinessException.class,
            exception -> assertThat(exception.errorCode()).isEqualTo(errorCode));
    verify(displayReviewRepository, never()).save(any());
  }

  private ImageInfo image(String name) {
    return new ImageInfo("https://cdn.test/" + name, 100, 100);
  }
}
