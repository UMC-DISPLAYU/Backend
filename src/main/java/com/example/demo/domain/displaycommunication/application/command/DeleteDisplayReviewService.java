package com.example.demo.domain.displaycommunication.application.command;

import com.example.demo.domain.displaycommunication.application.permission.DisplayCommunicationPermissionChecker;
import com.example.demo.domain.displaycommunication.application.result.DeletedDisplayReviewResult;
import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReview;
import com.example.demo.domain.displaycommunication.domain.repository.DisplayReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DeleteDisplayReviewService {
  private final DisplayReviewValidator displayReviewValidator;
  private final DisplayCommunicationPermissionChecker permissionChecker;
  private final DisplayReviewRepository displayReviewRepository;

  public DeletedDisplayReviewResult deleteReview(DeleteDisplayReviewCommand command) {
    displayReviewValidator.validateDisplayExists(command.displayId());
    displayReviewValidator.validateUserExists(command.userId());

    DisplayReview displayReview =
        displayReviewValidator.findReviewOrThrow(command.displayReviewId());

    displayReviewValidator.validateReviewTarget(displayReview, command.displayId());
    permissionChecker.requireReviewWriter(displayReview, command.userId());

    displayReview.delete();
    DisplayReview saved = displayReviewRepository.save(displayReview);

    return new DeletedDisplayReviewResult(saved.getDisplayReviewId(), saved.getDeletedAt());
  }
}
