package com.example.demo.domain.displaycommunication.application.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.domain.displaycommunication.application.permission.DisplayCommunicationPermissionChecker;
import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReview;
import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReviewReply;
import com.example.demo.domain.displaycommunication.domain.repository.DisplayReviewReplyRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeleteDisplayReviewReplyServiceTest {

  @Mock private DisplayReviewValidator displayReviewValidator;
  @Mock private DisplayCommunicationPermissionChecker permissionChecker;
  @Mock private DisplayReviewReplyRepository displayReviewReplyRepository;
  @InjectMocks private DeleteDisplayReviewReplyService service;

  @Test
  void replyCanBeDeletedAfterParentReviewIsDeleted() {
    DisplayReview deletedReview = DisplayReview.create(1L, 2L, "삭제된 후기", List.of());
    deletedReview.delete();
    DisplayReviewReply reply = DisplayReviewReply.create(10L, 3L, "답글", List.of());
    when(displayReviewValidator.findReviewIncludingDeletedOrThrow(10L)).thenReturn(deletedReview);
    when(displayReviewValidator.findReplyOrThrow(100L)).thenReturn(reply);
    when(displayReviewReplyRepository.save(reply)).thenReturn(reply);

    service.deleteReviewReply(new DeleteDisplayReviewReplyCommand(1L, 10L, 100L, 3L));

    assertThat(reply.isDeleted()).isTrue();
    verify(displayReviewValidator).findReviewIncludingDeletedOrThrow(10L);
    verify(displayReviewValidator).validateReviewTarget(deletedReview, 1L);
    verify(displayReviewValidator).validateReplyTarget(reply, 10L);
    verify(permissionChecker).requireReviewReplyWriter(reply, 3L);
    verify(displayReviewReplyRepository).save(reply);
  }
}
