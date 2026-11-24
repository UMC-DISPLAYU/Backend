package com.example.demo.domain.admin.infrastructure.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import com.example.demo.domain.admin.application.result.ParentDeletionResult;
import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkFeeling;
import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkFeelingReply;
import com.example.demo.domain.artworkcommunication.infrastructure.persistence.SpringDataArtworkFeelingJpaRepository;
import com.example.demo.domain.artworkcommunication.infrastructure.persistence.SpringDataArtworkFeelingReplyJpaRepository;
import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReview;
import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReviewReply;
import com.example.demo.domain.displaycommunication.infrastructure.persistence.SpringDataDisplayReviewJpaRepository;
import com.example.demo.domain.displaycommunication.infrastructure.persistence.SpringDataDisplayReviewReplyJpaRepository;
import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeeling;
import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeelingReply;
import com.example.demo.domain.personalartworkcommunication.infrastructure.persistence.SpringDataPersonalArtworkFeelingJpaRepository;
import com.example.demo.domain.personalartworkcommunication.infrastructure.persistence.SpringDataPersonalArtworkFeelingReplyJpaRepository;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AdminParentDeletionAdapterTest {
  private final SpringDataDisplayReviewJpaRepository displayReviews =
      mock(SpringDataDisplayReviewJpaRepository.class);
  private final SpringDataDisplayReviewReplyJpaRepository displayReplies =
      mock(SpringDataDisplayReviewReplyJpaRepository.class);
  private final SpringDataArtworkFeelingJpaRepository artworkFeelings =
      mock(SpringDataArtworkFeelingJpaRepository.class);
  private final SpringDataArtworkFeelingReplyJpaRepository artworkReplies =
      mock(SpringDataArtworkFeelingReplyJpaRepository.class);
  private final SpringDataPersonalArtworkFeelingJpaRepository personalFeelings =
      mock(SpringDataPersonalArtworkFeelingJpaRepository.class);
  private final SpringDataPersonalArtworkFeelingReplyJpaRepository personalReplies =
      mock(SpringDataPersonalArtworkFeelingReplyJpaRepository.class);
  private final AdminParentDeletionAdapter adapter =
      new AdminParentDeletionAdapter(
          displayReviews,
          displayReplies,
          artworkFeelings,
          artworkReplies,
          personalFeelings,
          personalReplies);

  @BeforeEach
  void resetRepositories() {
    reset(
        displayReviews,
        displayReplies,
        artworkFeelings,
        artworkReplies,
        personalFeelings,
        personalReplies);
  }

  @Test
  void deletesDisplayReviewAndAllActiveReplies() {
    DisplayReview review = DisplayReview.create(21L, 11L, "후기", List.of());
    DisplayReviewReply first = DisplayReviewReply.create(31L, 12L, "첫 답글", List.of());
    DisplayReviewReply second = DisplayReviewReply.create(31L, 13L, "둘째 답글", List.of());
    when(displayReviews.findActiveByIdForUpdate(31L)).thenReturn(Optional.of(review));
    when(displayReplies.findActiveByDisplayReviewIdForUpdate(31L))
        .thenReturn(List.of(first, second));
    when(displayReviews.save(review)).thenReturn(review);

    ParentDeletionResult result = adapter.delete("DISPLAY_REVIEW", 31L, 7L, "운영 검토");

    assertThat(review.isDeleted()).isTrue();
    assertThat(first.isDeleted()).isTrue();
    assertThat(second.isDeleted()).isTrue();
    assertThat(result.parentId()).isEqualTo(31L);
    assertThat(result.processedAt()).isEqualTo(review.getDeletedAt().toInstant(ZoneOffset.UTC));
    verify(displayReplies).saveAll(List.of(first, second));
    verifyNoInteractions(artworkFeelings, artworkReplies, personalFeelings, personalReplies);
  }

  @Test
  void deletesArtworkFeelingAndActiveReplies() {
    ArtworkFeeling feeling = ArtworkFeeling.create(21L, 11L, "방명록", List.of());
    ArtworkFeelingReply reply = ArtworkFeelingReply.create(31L, 12L, "답글", List.of());
    when(artworkFeelings.findActiveByIdForUpdate(31L)).thenReturn(Optional.of(feeling));
    when(artworkReplies.findActiveByFeelingIdForUpdate(31L)).thenReturn(List.of(reply));
    when(artworkFeelings.save(feeling)).thenReturn(feeling);

    adapter.delete("ARTWORK_FEELING", 31L, 7L, "운영 검토");

    assertThat(feeling.isDeleted()).isTrue();
    assertThat(reply.isDeleted()).isTrue();
    verifyNoInteractions(displayReviews, displayReplies, personalFeelings, personalReplies);
  }

  @Test
  void deletesPersonalArtworkFeelingAndActiveReplies() {
    PersonalArtworkFeeling feeling = PersonalArtworkFeeling.create(21L, 11L, "방명록", List.of());
    PersonalArtworkFeelingReply reply =
        PersonalArtworkFeelingReply.create(31L, 12L, "답글", List.of());
    when(personalFeelings.findActiveByIdForUpdate(31L)).thenReturn(Optional.of(feeling));
    when(personalReplies.findActiveByPersonalFeelingIdForUpdate(31L)).thenReturn(List.of(reply));
    when(personalFeelings.save(feeling)).thenReturn(feeling);

    adapter.delete("PERSONAL_ARTWORK_FEELING", 31L, 7L, "운영 검토");

    assertThat(feeling.isDeleted()).isTrue();
    assertThat(reply.isDeleted()).isTrue();
    verifyNoInteractions(displayReviews, displayReplies, artworkFeelings, artworkReplies);
  }

  @Test
  void missingOrAlreadyDeletedParentIsNotFoundWithoutDeletingReplies() {
    when(displayReviews.findActiveByIdForUpdate(31L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> adapter.delete("DISPLAY_REVIEW", 31L, 7L, "사유"))
        .isInstanceOfSatisfying(
            BusinessException.class,
            error -> assertThat(error.errorCode()).isEqualTo(GlobalErrorCode.NOT_FOUND));
    verifyNoInteractions(displayReplies);
  }
}
