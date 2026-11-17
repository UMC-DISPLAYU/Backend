package com.example.demo.domain.admin.infrastructure.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import com.example.demo.domain.admin.application.result.CommentDeletionResult;
import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkFeelingReply;
import com.example.demo.domain.artworkcommunication.infrastructure.persistence.SpringDataArtworkFeelingReplyJpaRepository;
import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReviewReply;
import com.example.demo.domain.displaycommunication.infrastructure.persistence.SpringDataDisplayReviewReplyJpaRepository;
import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeelingReply;
import com.example.demo.domain.personalartworkcommunication.infrastructure.persistence.SpringDataPersonalArtworkFeelingReplyJpaRepository;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AdminCommentDeletionAdapterTest {
  private final SpringDataDisplayReviewReplyJpaRepository displayReplies =
      mock(SpringDataDisplayReviewReplyJpaRepository.class);
  private final SpringDataArtworkFeelingReplyJpaRepository artworkReplies =
      mock(SpringDataArtworkFeelingReplyJpaRepository.class);
  private final SpringDataPersonalArtworkFeelingReplyJpaRepository personalReplies =
      mock(SpringDataPersonalArtworkFeelingReplyJpaRepository.class);
  private final AdminCommentDeletionAdapter adapter =
      new AdminCommentDeletionAdapter(displayReplies, artworkReplies, personalReplies);

  @BeforeEach
  void resetRepositories() {
    reset(displayReplies, artworkReplies, personalReplies);
  }

  @Test
  void deletesDisplayReviewReplyWithoutDeletingParent() {
    DisplayReviewReply reply = DisplayReviewReply.create(31L, 11L, "답글", List.of());
    when(displayReplies.findActiveByIdForUpdate(41L)).thenReturn(Optional.of(reply));
    when(displayReplies.save(reply)).thenReturn(reply);

    CommentDeletionResult result = adapter.delete("DISPLAY_REVIEW_REPLY", 41L, 7L, "운영 검토");

    assertThat(reply.isDeleted()).isTrue();
    assertThat(result.commentId()).isEqualTo(41L);
    assertThat(result.processedAt()).isEqualTo(reply.getDeletedAt().toInstant(ZoneOffset.UTC));
    verify(displayReplies).save(reply);
    verifyNoInteractions(artworkReplies, personalReplies);
  }

  @Test
  void deletesArtworkFeelingReply() {
    ArtworkFeelingReply reply = ArtworkFeelingReply.create(31L, 11L, "답글", List.of());
    when(artworkReplies.findActiveByIdForUpdate(41L)).thenReturn(Optional.of(reply));
    when(artworkReplies.save(reply)).thenReturn(reply);

    CommentDeletionResult result = adapter.delete("ARTWORK_FEELING_REPLY", 41L, 7L, "운영 검토");

    assertThat(reply.isDeleted()).isTrue();
    assertThat(result.processedAt()).isEqualTo(reply.getDeletedAt().toInstant(ZoneOffset.UTC));
    verifyNoInteractions(displayReplies, personalReplies);
  }

  @Test
  void deletesPersonalArtworkFeelingReply() {
    PersonalArtworkFeelingReply reply =
        PersonalArtworkFeelingReply.create(31L, 11L, "답글", List.of());
    when(personalReplies.findActiveByIdForUpdate(41L)).thenReturn(Optional.of(reply));
    when(personalReplies.save(reply)).thenReturn(reply);

    CommentDeletionResult result =
        adapter.delete("PERSONAL_ARTWORK_FEELING_REPLY", 41L, 7L, "운영 검토");

    assertThat(reply.isDeleted()).isTrue();
    assertThat(result.processedAt()).isEqualTo(reply.getDeletedAt().toInstant(ZoneOffset.UTC));
    verifyNoInteractions(displayReplies, artworkReplies);
  }

  @Test
  void missingOrAlreadyDeletedCommentIsNotFound() {
    when(displayReplies.findActiveByIdForUpdate(41L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> adapter.delete("DISPLAY_REVIEW_REPLY", 41L, 7L, "사유"))
        .isInstanceOfSatisfying(
            BusinessException.class,
            error -> assertThat(error.errorCode()).isEqualTo(GlobalErrorCode.NOT_FOUND));
    verify(displayReplies, never()).save(any());
  }
}
