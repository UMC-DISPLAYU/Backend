package com.example.demo.domain.displaycommunication.application.command;

import com.example.demo.domain.displaycommunication.application.permission.DisplayCommunicationPermissionChecker;
import com.example.demo.domain.displaycommunication.application.result.DisplayReviewReplyResult;
import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReview;
import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReviewReply;
import com.example.demo.domain.displaycommunication.domain.error.DisplayCommunicationErrorCode;
import com.example.demo.domain.displaycommunication.domain.repository.DisplayReviewAccessRepository.DisplayReviewAccess;
import com.example.demo.domain.displaycommunication.domain.repository.DisplayReviewReplyRepository;
import com.example.demo.domain.displaycommunication.domain.repository.UserExistenceRepository;
import com.example.demo.global.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateDisplayReviewReplyService {
  private final DisplayReviewValidator displayReviewValidator;
  private final DisplayCommunicationPermissionChecker permissionChecker;
  private final DisplayReviewReplyRepository displayReviewReplyRepository;
  private final UserExistenceRepository userExistenceRepository;

  public DisplayReviewReplyResult create(CreateDisplayReviewReplyCommand command) {
    displayReviewValidator.validateUserExists(command.userId());
    displayReviewValidator.validateReplyContent(command.content());
    displayReviewValidator.validateReplyImages(command.images());

    DisplayReview displayReview =
        displayReviewValidator.findReviewOrThrow(command.displayReviewId());

    displayReviewValidator.validateReviewTarget(displayReview, command.displayId());

    DisplayReviewAccess displayAccess =
        permissionChecker.requireDisplayAccess(command.displayId(), command.userId());

    DisplayReviewReply saved =
        displayReviewReplyRepository.save(
            DisplayReviewReply.create(
                command.displayReviewId(), command.userId(), command.content(), command.images()));

    String nickname =
        userExistenceRepository
            .findNicknameById(command.userId())
            .orElseThrow(() -> new BusinessException(DisplayCommunicationErrorCode.USER_NOT_FOUND));
    boolean isTeamMember =
        displayAccess.ownerUserId().equals(command.userId()) || displayAccess.acceptedTeamMember();

    return new DisplayReviewReplyResult(
        saved.getDisplayReviewReplyId(),
        saved.getCreatedAt(),
        saved.getContent(),
        saved.getDisplayReviewId(),
        saved.getUserId(),
        nickname,
        isTeamMember,
        saved.getImages().stream()
            .map(
                image ->
                    new DisplayReviewReplyResult.ImageResult(
                        image.getDisplayReviewReplyImageId(),
                        image.getImageUrl(),
                        image.getWidth(),
                        image.getHeight(),
                        image.getSortOrder()))
            .toList());
  }
}
