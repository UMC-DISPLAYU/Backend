package com.example.demo.domain.artworkcommunication.application.query;

import com.example.demo.domain.artworkcommunication.application.result.ArtworkQuestionListResult;
import com.example.demo.domain.artworkcommunication.application.result.ArtworkQuestionListResult.ArtworkQuestionItemResult;
import com.example.demo.domain.artworkcommunication.application.result.ArtworkQuestionListResult.ArtworkQuestionReplyItemResult;
import com.example.demo.domain.artworkcommunication.application.result.ArtworkQuestionListResult.ArtworkQuestionUserResult;
import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkQuestion;
import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkQuestionReply;
import com.example.demo.domain.artworkcommunication.domain.error.ArtworkCommunicationErrorCode;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkQuestionReplyRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkQuestionRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.CreatorExistenceRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.DisplayArtworkExistenceRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.UserExistenceRepository;
import com.example.demo.global.error.BusinessException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetArtworkQuestionsService {

  private static final int PAGE_SIZE = 3;

  private final DisplayArtworkExistenceRepository displayArtworkExistenceRepository;
  private final ArtworkQuestionRepository artworkQuestionRepository;
  private final ArtworkQuestionReplyRepository artworkQuestionReplyRepository;
  private final UserExistenceRepository userExistenceRepository;
  private final CreatorExistenceRepository creatorExistenceRepository;

  public ArtworkQuestionListResult getQuestions(GetArtworkQuestionsQuery query) {
    validateDisplayArtworkExists(query.displayArtworkId());

    List<ArtworkQuestion> fetched =
        artworkQuestionRepository.findActiveByDisplayArtworkIdWithCursor(
            query.displayArtworkId(), query.cursorId(), PAGE_SIZE + 1);
    boolean hasNext = fetched.size() > PAGE_SIZE;
    List<ArtworkQuestion> pageQuestions = hasNext ? fetched.subList(0, PAGE_SIZE) : fetched;

    if (pageQuestions.isEmpty()) {
      return new ArtworkQuestionListResult(List.of(), null, PAGE_SIZE, false);
    }

    Map<Long, List<ArtworkQuestionReply>> repliesByQuestionId =
        findRepliesByQuestionId(pageQuestions);

    List<ArtworkQuestionItemResult> questions =
        pageQuestions.stream()
            .map(
                question ->
                    toQuestionItem(
                        query.displayArtworkId(),
                        question,
                        repliesByQuestionId.getOrDefault(question.getArtQueId(), List.of())))
            .toList();

    Long nextCursorId = hasNext ? questions.get(questions.size() - 1).questionId() : null;
    return new ArtworkQuestionListResult(questions, nextCursorId, PAGE_SIZE, hasNext);
  }

  private void validateDisplayArtworkExists(Long displayArtworkId) {
    if (!displayArtworkExistenceRepository.existsById(displayArtworkId)) {
      throw new BusinessException(ArtworkCommunicationErrorCode.ARTWORK_NOT_FOUND);
    }
  }

  private Map<Long, List<ArtworkQuestionReply>> findRepliesByQuestionId(
      List<ArtworkQuestion> questions) {
    List<Long> questionIds = questions.stream().map(ArtworkQuestion::getArtQueId).toList();
    return artworkQuestionReplyRepository.findActiveByQuestionIds(questionIds).stream()
        .collect(Collectors.groupingBy(ArtworkQuestionReply::getArtQueId));
  }

  private ArtworkQuestionItemResult toQuestionItem(
      Long displayArtworkId, ArtworkQuestion question, List<ArtworkQuestionReply> replies) {
    ArtworkQuestionUserResult user =
        new ArtworkQuestionUserResult(
            question.getUserId(), findUserNicknameOrThrow(question.getUserId()));

    ArtworkQuestionReplyItemResult reply =
        replies.stream()
            .findFirst()
            .map(firstReply -> toReplyItem(displayArtworkId, firstReply))
            .orElse(null);

    return new ArtworkQuestionItemResult(
        question.getArtQueId(),
        question.getContent(),
        question.getIsPublic(),
        question.getAnswerStatus().name(),
        question.getCreatedAt(),
        user,
        reply);
  }

  private ArtworkQuestionReplyItemResult toReplyItem(
      Long displayArtworkId, ArtworkQuestionReply reply) {
    String creatorName = findContactCreatorNameOrNull(displayArtworkId);

    return new ArtworkQuestionReplyItemResult(
        creatorName, creatorName != null, reply.getContent(), reply.getCreatedAt());
  }

  private String findContactCreatorNameOrNull(Long displayArtworkId) {
    return creatorExistenceRepository
        .findContactCreatorByDisplayArtworkId(displayArtworkId)
        .map(CreatorExistenceRepository.ContactCreator::creatorName)
        .orElse(null);
  }

  private String findUserNicknameOrThrow(Long userId) {
    return userExistenceRepository
        .findNicknameById(userId)
        .orElseThrow(() -> new BusinessException(ArtworkCommunicationErrorCode.USER_NOT_FOUND));
  }
}
