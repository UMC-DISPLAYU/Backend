package com.example.demo.domain.displaycommunication.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReview;
import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReviewReply;
import com.example.demo.global.config.JpaAuditingConfig;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@Import(JpaAuditingConfig.class)
class DisplayReviewJpaRepositoryTest {

  @Autowired private DisplayReviewJpaRepository displayReviewJpaRepository;
  @Autowired private DisplayReviewReplyJpaRepository displayReviewReplyJpaRepository;

  @Test
  void activeReviewIsReturnedWithoutActiveReplies() {
    DisplayReview activeReview =
        displayReviewJpaRepository.saveAndFlush(DisplayReview.create(1L, 1L, "활성 후기", List.of()));

    List<DisplayReview> reviews =
        displayReviewJpaRepository.findByDisplayIdWithCursor(1L, null, PageRequest.of(0, 10));

    assertThat(reviews)
        .extracting(DisplayReview::getDisplayReviewId)
        .containsExactly(activeReview.getDisplayReviewId());
  }

  @Test
  void deletedReviewIsReturnedOnlyWhileActiveReplyExists() {
    DisplayReview deletedReview =
        displayReviewJpaRepository.saveAndFlush(DisplayReview.create(1L, 1L, "삭제할 후기", List.of()));
    deletedReview.delete();
    displayReviewJpaRepository.saveAndFlush(deletedReview);
    DisplayReviewReply reply =
        displayReviewReplyJpaRepository.saveAndFlush(
            DisplayReviewReply.create(deletedReview.getDisplayReviewId(), 2L, "활성 답글", List.of()));

    List<DisplayReview> reviewsWithActiveReply =
        displayReviewJpaRepository.findByDisplayIdWithCursor(1L, null, PageRequest.of(0, 10));

    assertThat(reviewsWithActiveReply)
        .extracting(DisplayReview::getDisplayReviewId)
        .containsExactly(deletedReview.getDisplayReviewId());

    reply.delete();
    displayReviewReplyJpaRepository.saveAndFlush(reply);

    List<DisplayReview> reviewsAfterLastReplyDeleted =
        displayReviewJpaRepository.findByDisplayIdWithCursor(1L, null, PageRequest.of(0, 10));

    assertThat(reviewsAfterLastReplyDeleted).isEmpty();
  }
}
