package com.example.demo.domain.displaycommunication.application.command;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.domain.display.application.result.DisplayReviewAccessResult;
import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReview;
import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReviewReply;
import com.example.demo.domain.displaycommunication.domain.repository.DisplayReviewReplyRepository;
import com.example.demo.domain.displaycommunication.domain.repository.UserExistenceRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateDisplayReviewReplyServiceTest {

  @Mock private DisplayReviewValidator displayReviewValidator;
  @Mock private DisplayReviewReplyRepository displayReviewReplyRepository;
  @Mock private UserExistenceRepository userExistenceRepository;
  @InjectMocks private CreateDisplayReviewReplyService service;

  @Test
  void replyCanBeCreatedAfterParentReviewIsDeleted() {
    DisplayReview deletedReview = DisplayReview.create(1L, 2L, "삭제된 후기", List.of());
    deletedReview.delete();
    DisplayReviewReply reply = DisplayReviewReply.create(10L, 3L, "답글", List.of());
    DisplayReviewAccessResult access =
        new DisplayReviewAccessResult(
            2L, LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31), true, Set.of());
    when(displayReviewValidator.findReviewIncludingDeletedOrThrow(10L)).thenReturn(deletedReview);
    when(displayReviewValidator.findDisplayAccessOrThrow(1L)).thenReturn(access);
    when(displayReviewReplyRepository.save(org.mockito.ArgumentMatchers.any())).thenReturn(reply);
    when(userExistenceRepository.findNicknameById(3L)).thenReturn(java.util.Optional.of("사용자"));

    service.create(new CreateDisplayReviewReplyCommand(1L, 10L, 3L, "답글", List.of()));

    verify(displayReviewValidator).findReviewIncludingDeletedOrThrow(10L);
    verify(displayReviewValidator).validateReviewTarget(deletedReview, 1L);
    verify(displayReviewReplyRepository).save(org.mockito.ArgumentMatchers.any());
  }
}
