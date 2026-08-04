package com.example.demo.domain.artworkcommunication.application.command;

import com.example.demo.domain.artworkcommunication.application.result.ArtworkQuestionReplyLikeResult;
import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkQuestion;
import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkQuestionReply;
import com.example.demo.domain.artworkcommunication.domain.error.ArtworkCommunicationErrorCode;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkQuestionReplyLikeRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkQuestionReplyLikeRepository.ArtworkQuestionReplyLikeSnapshot;
import com.example.demo.global.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ArtworkQuestionReplyLikeService {

  private final ArtworkQuestionReplyLikeRepository artworkQuestionReplyLikeRepository;
  private final ArtworkQuestionValidator artworkQuestionValidator;

  public ArtworkQuestionReplyLikeResult toggleQuestionReplyLike(
      ArtworkQuestionReplyLikeCommand command) {
    artworkQuestionValidator.validateDisplayArtworkExists(command.displayArtworkId());
    artworkQuestionValidator.validateUserExists(command.userId());

    ArtworkQuestion question =
        artworkQuestionValidator.findActiveQuestionForUpdateOrThrow(command.questionId());
    artworkQuestionValidator.validateQuestionTarget(question, command.displayArtworkId());
    artworkQuestionValidator.validateLikePermission(
        question, command.displayArtworkId(), command.userId());

    ArtworkQuestionReply reply =
        artworkQuestionValidator.findActiveReplyForUpdateOrThrow(command.questionReplyId());
    artworkQuestionValidator.validateReplyTarget(reply, command.questionId());

    ArtworkQuestionReplyLikeSnapshot snapshot =
        artworkQuestionReplyLikeRepository
            .toggleAndGetSnapshot(command.questionReplyId(), command.userId())
            .orElseThrow(
                () ->
                    new BusinessException(ArtworkCommunicationErrorCode.QUESTION_REPLY_NOT_FOUND));

    return new ArtworkQuestionReplyLikeResult(
        snapshot.questionReplyId(),
        snapshot.liked(),
        Math.toIntExact(snapshot.likeCount()),
        snapshot.createdAt(),
        snapshot.deletedAt());
  }
}
