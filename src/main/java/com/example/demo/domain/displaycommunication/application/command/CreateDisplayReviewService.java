package com.example.demo.domain.displaycommunication.application.command;

import com.example.demo.domain.displaycommunication.application.result.DisplayReviewResult;
import com.example.demo.domain.displaycommunication.application.result.DisplayReviewResult.ImageResult;
import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReview;
import com.example.demo.domain.displaycommunication.domain.repository.DisplayReviewRepository;
import lombok.RequiredArgsConstructor;
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
    displayReviewValidator.validateDisplayIsWritable(displayAccess);
    displayReviewValidator.validateContent(command.content());
    displayReviewValidator.validateImages(command.images());

    DisplayReview saved =
        displayReviewRepository.save(
            DisplayReview.create(
                command.displayId(), command.userId(), command.content(), command.images()));

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
}
