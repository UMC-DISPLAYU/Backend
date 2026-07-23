package com.example.demo.domain.displaycommunication.application.command;

import com.example.demo.domain.displaycommunication.application.result.DisplayReviewResult;
import com.example.demo.domain.displaycommunication.application.result.DisplayReviewResult.ImageResult;
import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReview;
import com.example.demo.domain.displaycommunication.domain.error.DisplayCommunicationErrorCode;
import com.example.demo.domain.displaycommunication.domain.repository.DisplayReviewRepository;
import com.example.demo.global.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateDisplayReviewService {
  private final DisplayReviewValidator displayReviewValidator;
  private final DisplayReviewRepository displayReviewRepository;

  public DisplayReviewResult create(CreateDisplayReviewCommand command) {
    var displayAccess =
        displayReviewValidator.findDisplayAccessOrThrow(command.displayId(), command.userId());
    displayReviewValidator.validateUserExists(command.userId());
    displayReviewValidator.validateDisplayIsOngoing(displayAccess);
    displayReviewValidator.validateContent(command.content());
    displayReviewValidator.validateImages(command.images());
    displayReviewValidator.validateNotDisplayTeamMember(displayAccess, command.userId());
    displayReviewValidator.validateReviewNotExists(command.displayId(), command.userId());

    DisplayReview saved;
    try {
      saved =
          displayReviewRepository.save(
              DisplayReview.create(
                  command.displayId(), command.userId(), command.content(), command.images()));
    } catch (DataIntegrityViolationException exception) {
      if (isDisplayReviewUniqueConstraintViolation(exception)) {
        throw new BusinessException(
            DisplayCommunicationErrorCode.DISPLAY_REVIEW_ALREADY_EXISTS, exception);
      }
      throw exception;
    }

    return new DisplayReviewResult(
        saved.getDisplayReviewId(),
        saved.getContent(),
        saved.getCreatedAt(),
        saved.getDisplayId(),
        saved.getUserId(),
        saved.getImages().stream()
            .map(
                image ->
                    new ImageResult(
                        image.getReviewImageId(),
                        image.getImageUrl(),
                        image.getWidth(),
                        image.getHeight(),
                        image.getSortOrder()))
            .toList());
  }

  private boolean isDisplayReviewUniqueConstraintViolation(
      DataIntegrityViolationException exception) {
    String message = exception.getMostSpecificCause().getMessage();
    return message != null && message.contains("UQ_DISPLAY_REVIEW_DISPLAY_USER");
  }
}
