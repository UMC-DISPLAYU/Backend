package com.example.demo.domain.displaycommunication.application.command;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReview;
import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReviewReply;
import com.example.demo.domain.displaycommunication.domain.repository.DisplayReviewReplyLikeRepository;
import com.example.demo.domain.displaycommunication.domain.repository.DisplayReviewReplyLikeRepository.DisplayReviewReplyLikeSnapshot;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DisplayReviewReplyLikeServiceTest {

  @Mock private DisplayReviewValidator displayReviewValidator;
  @Mock private DisplayReviewReplyLikeRepository displayReviewReplyLikeRepository;
  @InjectMocks private DisplayReviewReplyLikeService service;

  @Test
  void replyCanBeLikedAfterParentReviewIsDeleted() {
    DisplayReview deletedReview = DisplayReview.create(1L, 2L, "삭제된 후기", List.of());
    deletedReview.delete();
    DisplayReviewReply reply = DisplayReviewReply.create(10L, 3L, "답글", List.of());
    when(displayReviewValidator.findReviewIncludingDeletedOrThrow(10L)).thenReturn(deletedReview);
    when(displayReviewValidator.findReplyOrThrow(100L)).thenReturn(reply);
    when(displayReviewReplyLikeRepository.likeAndGetSnapshot(100L, 3L))
        .thenReturn(Optional.of(new DisplayReviewReplyLikeSnapshot(100L, true, 1L, null, null)));

    service.likeReviewReply(new DisplayReviewReplyLikeCommand(1L, 10L, 100L, 3L));

    verify(displayReviewValidator).findReviewIncludingDeletedOrThrow(10L);
    verify(displayReviewValidator).validateReviewTarget(deletedReview, 1L);
    verify(displayReviewValidator).validateReplyTarget(reply, 10L);
    verify(displayReviewReplyLikeRepository).likeAndGetSnapshot(100L, 3L);
  }
}
