package com.example.demo.domain.displaycommunication.application.command;

import com.example.demo.domain.displaycommunication.application.permission.DisplayReviewPermissionChecker;
import com.example.demo.domain.displaycommunication.application.result.DeletedDisplayReviewReplyResult;
import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReview;
import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReviewReply;
import com.example.demo.domain.displaycommunication.domain.repository.DisplayReviewReplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteDisplayReviewReplyService {

  private final DisplayReviewValidator displayReviewValidator;
  private final DisplayReviewPermissionChecker permissionChecker;
  private final DisplayReviewReplyRepository displayReviewReplyRepository;

  @Transactional
  public DeletedDisplayReviewReplyResult deleteReviewReply(
      DeleteDisplayReviewReplyCommand command) {
    displayReviewValidator.validateDisplayExists(command.displayId());
    displayReviewValidator.validateUserExists(command.userId());

    DisplayReview displayReview =
        displayReviewValidator.findReviewIncludingDeletedOrThrow(command.displayReviewId());
    displayReviewValidator.validateReviewTarget(displayReview, command.displayId());

    DisplayReviewReply displayReviewReply =
        displayReviewValidator.findReplyOrThrow(command.displayReviewReplyId());
    displayReviewValidator.validateReplyTarget(displayReviewReply, command.displayReviewId());
    permissionChecker.requireReplyWriter(displayReviewReply, command.userId());

    displayReviewReply.delete();
    DisplayReviewReply saved = displayReviewReplyRepository.save(displayReviewReply);

    return new DeletedDisplayReviewReplyResult(
        saved.getDisplayReviewReplyId(), saved.getDeletedAt());
  }
}
