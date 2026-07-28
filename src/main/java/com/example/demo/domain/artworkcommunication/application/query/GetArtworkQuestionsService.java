package com.example.demo.domain.artworkcommunication.application.query;

import com.example.demo.domain.artworkcommunication.application.command.ArtworkQuestionValidator;
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
import java.util.Objects;
import java.util.Set;
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
  private final ArtworkQuestionValidator artworkQuestionValidator;

  public ArtworkQuestionListResult getQuestions(GetArtworkQuestionsQuery query) {
    artworkQuestionValidator.validateDisplayArtworkExists(query.displayArtworkId());

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
    Map<Long, String> nicknameByUserId = findQuestionUserNicknames(pageQuestions);
    Map<Long, String> creatorNameById = findCreatorNamesById(repliesByQuestionId);

    List<ArtworkQuestionItemResult> questions =
        pageQuestions.stream()
            .map(
                question ->
                    toQuestionItem(
                        query.displayArtworkId(),
                        question,
                        repliesByQuestionId.getOrDefault(question.getQuestionId(), List.of()),
                        nicknameByUserId,
                        creatorNameById))
            .toList();

    Long nextCursorId = hasNext ? questions.get(questions.size() - 1).questionId() : null;
    return new ArtworkQuestionListResult(questions, nextCursorId, PAGE_SIZE, hasNext);
  }

  private Map<Long, List<ArtworkQuestionReply>> findRepliesByQuestionId(
      List<ArtworkQuestion> questions) {
    List<Long> questionIds = questions.stream().map(ArtworkQuestion::getQuestionId).toList();
    return artworkQuestionReplyRepository.findActiveByQuestionIds(questionIds).stream()
        .collect(Collectors.groupingBy(ArtworkQuestionReply::getQuestionId));
  }

  private ArtworkQuestionItemResult toQuestionItem(
      Long displayArtworkId,
      ArtworkQuestion question,
      List<ArtworkQuestionReply> replies,
      Map<Long, String> nicknameByUserId,
      Map<Long, String> creatorNameById) {
    ArtworkQuestionUserResult user =
        new ArtworkQuestionUserResult(
            question.getUserId(), getNicknameOrThrow(nicknameByUserId, question.getUserId()));

    ArtworkQuestionReplyItemResult reply =
        replies.stream()
            .findFirst()
            .map(firstReply -> toReplyItem(displayArtworkId, firstReply, creatorNameById))
            .orElse(null);

    return new ArtworkQuestionItemResult(
        question.getQuestionId(),
        question.getContent(),
        question.getIsPublic(),
        question.getAnswerStatus().name(),
        question.getCreatedAt(),
        user,
        reply);
  }

  private ArtworkQuestionReplyItemResult toReplyItem(
      Long displayArtworkId, ArtworkQuestionReply reply, Map<Long, String> creatorNameById) {
    Long creatorId = reply.getCreatorId();
    String creatorName = creatorId == null ? null : creatorNameById.get(creatorId);
    if (creatorName == null) {
      creatorName = findContactCreatorNameOrNull(displayArtworkId);
    }

    return new ArtworkQuestionReplyItemResult(
        creatorId, creatorName, creatorName != null, reply.getContent(), reply.getCreatedAt());
  }

  private Map<Long, String> findCreatorNamesById(
      Map<Long, List<ArtworkQuestionReply>> repliesByQuestionId) {
    Set<Long> creatorIds =
        repliesByQuestionId.values().stream()
            .flatMap(List::stream)
            .map(ArtworkQuestionReply::getCreatorId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

    return creatorExistenceRepository.findCreatorNamesByIds(creatorIds);
  }

  private Map<Long, String> findQuestionUserNicknames(List<ArtworkQuestion> questions) {
    Set<Long> userIds =
        questions.stream().map(ArtworkQuestion::getUserId).collect(Collectors.toSet());

    return userExistenceRepository.findNicknamesByIds(userIds);
  }

  private String findContactCreatorNameOrNull(Long displayArtworkId) {
    return creatorExistenceRepository
        .findContactCreatorByDisplayArtworkId(displayArtworkId)
        .map(CreatorExistenceRepository.ContactCreator::creatorName)
        .orElse(null);
  }

  private String getNicknameOrThrow(Map<Long, String> nicknameByUserId, Long userId) {
    String nickname = nicknameByUserId.get(userId);
    if (nickname == null) {
      throw new BusinessException(ArtworkCommunicationErrorCode.USER_NOT_FOUND);
    }

    return nickname;
  }
}
