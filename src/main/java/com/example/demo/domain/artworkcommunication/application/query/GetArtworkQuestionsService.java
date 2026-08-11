package com.example.demo.domain.artworkcommunication.application.query;

import com.example.demo.domain.artworkcommunication.application.command.ArtworkQuestionValidator;
import com.example.demo.domain.artworkcommunication.application.permission.ArtworkCommunicationPermissionChecker;
import com.example.demo.domain.artworkcommunication.application.result.ArtworkQuestionListResult;
import com.example.demo.domain.artworkcommunication.application.result.ArtworkQuestionListResult.ArtworkQuestionItemResult;
import com.example.demo.domain.artworkcommunication.application.result.ArtworkQuestionListResult.ArtworkQuestionReplyItemResult;
import com.example.demo.domain.artworkcommunication.application.result.ArtworkQuestionListResult.ArtworkQuestionUserResult;
import com.example.demo.domain.artworkcommunication.application.result.ArtworkQuestionListResult.QuestionImageResult;
import com.example.demo.domain.artworkcommunication.application.result.ArtworkQuestionListResult.ReplyImageResult;
import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkQuestion;
import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkQuestionReply;
import com.example.demo.domain.artworkcommunication.domain.error.ArtworkCommunicationErrorCode;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkQuestionReplyRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkQuestionRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.CreatorExistenceRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.CreatorExistenceRepository.ContactCreator;
import com.example.demo.domain.artworkcommunication.domain.repository.UserExistenceRepository;
import com.example.demo.global.error.BusinessException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetArtworkQuestionsService {

  private static final int MAX_PAGE_SIZE = 50;

  private final ArtworkQuestionRepository artworkQuestionRepository;
  private final ArtworkQuestionReplyRepository artworkQuestionReplyRepository;
  private final UserExistenceRepository userExistenceRepository;
  private final CreatorExistenceRepository creatorExistenceRepository;
  private final ArtworkQuestionValidator artworkQuestionValidator;
  private final ArtworkCommunicationPermissionChecker permissionChecker;

  @Transactional(readOnly = true)
  public ArtworkQuestionListResult getQuestions(GetArtworkQuestionsQuery query) {
    artworkQuestionValidator.validateDisplayArtworkExists(query.displayArtworkId());
    int pageSize = Math.min(Math.max(query.size(), 1), MAX_PAGE_SIZE);

    boolean isParticipant =
        query.userId() != null
            && creatorExistenceRepository
                .findParticipantNameByDisplayArtworkIdAndUserId(
                    query.displayArtworkId(), query.userId())
                .isPresent();
    Optional<ContactCreator> contactCreator =
        query.userId() != null
            ? creatorExistenceRepository.findContactCreatorByDisplayArtworkIdAndUserId(
                query.displayArtworkId(), query.userId())
            : Optional.empty();
    boolean isContact = contactCreator.isPresent();

    List<ArtworkQuestion> fetched =
        artworkQuestionRepository.findActiveByDisplayArtworkIdWithCursor(
            query.displayArtworkId(), query.cursorId(), pageSize + 1);
    boolean hasNext = fetched.size() > pageSize;
    List<ArtworkQuestion> pageQuestions = hasNext ? fetched.subList(0, pageSize) : fetched;

    if (pageQuestions.isEmpty()) {
      return new ArtworkQuestionListResult(List.of(), null, pageSize, false);
    }

    Map<Long, List<ArtworkQuestionReply>> repliesByQuestionId =
        findRepliesByQuestionId(pageQuestions);
    Map<Long, String> nicknameByUserId = findQuestionUserNicknames(pageQuestions);
    Map<Long, String> creatorNameByUserId = findQuestionCreatorNames(pageQuestions, query);
    Map<Long, String> creatorNameById = findCreatorNamesById(repliesByQuestionId);
    Map<Long, Long> questionLikeCounts = Map.of();
    Set<Long> likedQuestionIds = Set.of();
    Map<Long, Long> replyLikeCounts = Map.of();
    Set<Long> likedQuestionReplyIds = Set.of();

    List<ArtworkQuestionItemResult> questions =
        pageQuestions.stream()
            .map(
                question ->
                    toQuestionItem(
                        query.displayArtworkId(),
                        question,
                        repliesByQuestionId.getOrDefault(question.getQuestionId(), List.of()),
                        nicknameByUserId,
                        creatorNameByUserId,
                        creatorNameById,
                        questionLikeCounts,
                        likedQuestionIds,
                        replyLikeCounts,
                        likedQuestionReplyIds,
                        query.userId(),
                        contactCreator.map(ContactCreator::creatorId).orElse(null),
                        isParticipant,
                        isContact))
            .toList();

    Long nextCursorId = hasNext ? questions.get(questions.size() - 1).questionId() : null;
    return new ArtworkQuestionListResult(questions, nextCursorId, pageSize, hasNext);
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
      Map<Long, String> creatorNameByUserId,
      Map<Long, String> creatorNameById,
      Map<Long, Long> questionLikeCounts,
      Set<Long> likedQuestionIds,
      Map<Long, Long> replyLikeCounts,
      Set<Long> likedQuestionReplyIds,
      Long userId,
      Long currentCreatorId,
      boolean isParticipant,
      boolean isContact) {
    boolean accessible = permissionChecker.isQuestionAccessible(question, userId, isParticipant);
    boolean canReply = accessible && isContact && !question.isAnswered();

    if (!accessible) {
      return new ArtworkQuestionItemResult(
          question.getQuestionId(),
          null,
          question.getIsPublic(),
          false,
          false,
          false,
          null,
          false,
          question.getAnswerStatus().name(),
          question.getCreatedAt(),
          List.of(),
          null,
          null);
    }

    ArtworkQuestionUserResult user =
        new ArtworkQuestionUserResult(
            question.getUserId(),
            getNicknameOrThrow(nicknameByUserId, question.getUserId()),
            creatorNameByUserId.containsKey(question.getUserId()));

    ArtworkQuestionReplyItemResult reply =
        replies.stream()
            .findFirst()
            .map(
                firstReply ->
                    toReplyItem(
                        displayArtworkId,
                        firstReply,
                        creatorNameById,
                        replyLikeCounts,
                        likedQuestionReplyIds,
                        currentCreatorId))
            .orElse(null);

    return new ArtworkQuestionItemResult(
        question.getQuestionId(),
        question.getContent(),
        question.getIsPublic(),
        true,
        Objects.equals(question.getUserId(), userId),
        canReply,
        questionLikeCounts.getOrDefault(question.getQuestionId(), 0L),
        likedQuestionIds.contains(question.getQuestionId()),
        question.getAnswerStatus().name(),
        question.getCreatedAt(),
        question.getImages().stream()
            .map(
                image ->
                    new QuestionImageResult(
                        image.getQuestionImageId(),
                        image.getImageUrl(),
                        image.getWidth(),
                        image.getHeight(),
                        image.getSortOrder()))
            .toList(),
        user,
        reply);
  }

  private ArtworkQuestionReplyItemResult toReplyItem(
      Long displayArtworkId,
      ArtworkQuestionReply reply,
      Map<Long, String> creatorNameById,
      Map<Long, Long> replyLikeCounts,
      Set<Long> likedQuestionReplyIds,
      Long currentCreatorId) {
    Long creatorId = reply.getCreatorId();
    String creatorName = creatorId == null ? null : creatorNameById.get(creatorId);
    if (creatorName == null) {
      creatorName = findContactCreatorNameOrNull(displayArtworkId);
    }

    return new ArtworkQuestionReplyItemResult(
        reply.getQueReplyId(),
        creatorId,
        creatorName,
        creatorName != null,
        reply.getContent(),
        reply.getCreatedAt(),
        reply.getImages().stream()
            .map(
                image ->
                    new ReplyImageResult(
                        image.getQuestionReplyImageId(),
                        image.getImageUrl(),
                        image.getWidth(),
                        image.getHeight(),
                        image.getSortOrder()))
            .toList(),
        replyLikeCounts.getOrDefault(reply.getQueReplyId(), 0L),
        likedQuestionReplyIds.contains(reply.getQueReplyId()),
        currentCreatorId != null && Objects.equals(creatorId, currentCreatorId));
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

  private Map<Long, String> findQuestionCreatorNames(
      List<ArtworkQuestion> questions, GetArtworkQuestionsQuery query) {
    Set<Long> userIds =
        questions.stream().map(ArtworkQuestion::getUserId).collect(Collectors.toSet());

    return creatorExistenceRepository.findCreatorNamesByDisplayArtworkIdAndUserIds(
        query.displayArtworkId(), userIds);
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
