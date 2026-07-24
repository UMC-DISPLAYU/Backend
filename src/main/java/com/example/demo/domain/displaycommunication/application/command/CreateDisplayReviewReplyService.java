package com.example.demo.domain.displaycommunication.application.command;

import com.example.demo.domain.display.domain.error.DisplayErrorCode;
import com.example.demo.domain.displaycommunication.application.result.DisplayReviewReplyResult;
import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReview;
import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReviewReply;
import com.example.demo.domain.displaycommunication.domain.error.DisplayCommunicationErrorCode;
import com.example.demo.domain.displaycommunication.domain.repository.DisplayReviewAccessRepository;
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
  private final DisplayReviewReplyValidator validator;
  private final DisplayReviewReplyRepository displayReviewReplyRepository;
  private final DisplayReviewAccessRepository displayReviewAccessRepository;
  private final UserExistenceRepository userExistenceRepository;

  public DisplayReviewReplyResult create(CreateDisplayReviewReplyCommand command) {
    validator.validateUserExists(command.userId());
    validator.validateContent(command.content());

    DisplayReview displayReview = validator.findReviewOrThrow(command.displayReviewId());
    validator.validateReplyTarget(displayReview, command.displayId());

    DisplayReviewAccess displayAccess =
        displayReviewAccessRepository
            .findByDisplayIdAndUserId(command.displayId(), command.userId())
            .orElseThrow(() -> new BusinessException(DisplayErrorCode.DISPLAY_NOT_FOUND));

    DisplayReviewReply saved =
        displayReviewReplyRepository.save(
            DisplayReviewReply.create(
                command.displayReviewId(), command.userId(), command.content()));

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
        isTeamMember);
  }
}
