package com.example.demo.domain.displaycommunication.application.command;

import com.example.demo.domain.displaycommunication.application.result.DeletedDisplayReviewReplyResult;
import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReview;
import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReviewReply;
import com.example.demo.domain.displaycommunication.domain.repository.DisplayReviewReplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DeleteDisplayReviewReplyService {

  private final DisplayReviewValidator displayReviewValidator;
  private final DisplayReviewReplyRepository displayReviewReplyRepository;

  public DeletedDisplayReviewReplyResult deleteReviewReply(
      DeleteDisplayReviewReplyCommand command) {
    displayReviewValidator.validateDisplayExists(command.displayId());
    displayReviewValidator.validateUserExists(command.userId());

    DisplayReview displayReview =
        displayReviewValidator.findReviewIncludingDeletedOrThrow(command.displayReviewId());
    displayReviewValidator.validateReviewTarget(displayReview, command.displayId());

    DisplayReviewReply displayReviewReply =
        displayReviewValidator.findReplyOrThrow(command.displayReviewReplyId());
    displayReviewValidator.validateAccessibleReply(
        displayReviewReply, command.displayReviewId(), command.userId());

    displayReviewReply.delete();
    DisplayReviewReply saved = displayReviewReplyRepository.save(displayReviewReply);

    return new DeletedDisplayReviewReplyResult(
        saved.getDisplayReviewReplyId(), saved.getDeletedAt());
  }
}
