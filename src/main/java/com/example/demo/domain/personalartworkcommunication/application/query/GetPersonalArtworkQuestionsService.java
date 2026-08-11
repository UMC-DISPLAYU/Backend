package com.example.demo.domain.personalartworkcommunication.application.query;

import com.example.demo.domain.personalartworkcommunication.application.command.PersonalArtworkQuestionValidator;
import com.example.demo.domain.personalartworkcommunication.application.result.PersonalArtworkQuestionListResult;
import com.example.demo.domain.personalartworkcommunication.application.result.PersonalArtworkQuestionListResult.PersonalArtworkQuestionItemResult;
import com.example.demo.domain.personalartworkcommunication.application.result.PersonalArtworkQuestionListResult.PersonalArtworkQuestionReplyItemResult;
import com.example.demo.domain.personalartworkcommunication.application.result.PersonalArtworkQuestionListResult.PersonalArtworkQuestionUserResult;
import com.example.demo.domain.personalartworkcommunication.application.result.PersonalArtworkQuestionListResult.QuestionImageResult;
import com.example.demo.domain.personalartworkcommunication.application.result.PersonalArtworkQuestionListResult.ReplyImageResult;
import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkQuestion;
import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkQuestionReply;
import com.example.demo.domain.personalartworkcommunication.domain.error.PersonalArtworkCommunicationErrorCode;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkQuestionReplyRepository;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkQuestionRepository;
import com.example.demo.domain.personalartworkcommunication.domain.repository.UserExistenceRepository;
import com.example.demo.global.error.BusinessException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetPersonalArtworkQuestionsService {

  private static final int MAX_PAGE_SIZE = 50;

  private final PersonalArtworkQuestionRepository personalArtworkQuestionRepository;
  private final PersonalArtworkQuestionReplyRepository personalArtworkQuestionReplyRepository;
  private final UserExistenceRepository userExistenceRepository;
  private final PersonalArtworkQuestionValidator personalArtworkQuestionValidator;

  @Transactional(readOnly = true)
  public PersonalArtworkQuestionListResult getQuestions(GetPersonalArtworkQuestionsQuery query) {
    int pageSize = Math.min(Math.max(query.size(), 1), MAX_PAGE_SIZE);
    Long ownerUserId =
        personalArtworkQuestionValidator
            .findPersonalArtworkAccessOrThrow(query.personalArtworkId())
            .ownerUserId();

    List<PersonalArtworkQuestion> fetched =
        personalArtworkQuestionRepository.findActiveByPersonalArtworkIdWithCursor(
            query.personalArtworkId(), query.cursorId(), pageSize + 1);
    boolean hasNext = fetched.size() > pageSize;
    List<PersonalArtworkQuestion> pageQuestions = hasNext ? fetched.subList(0, pageSize) : fetched;

    if (pageQuestions.isEmpty()) {
      return new PersonalArtworkQuestionListResult(List.of(), null, pageSize, false);
    }

    Map<Long, PersonalArtworkQuestionReply> replyByQuestionId =
        findRepliesByQuestionId(pageQuestions);
    Set<Long> userIds = collectUserIds(pageQuestions, replyByQuestionId.values());
    Map<Long, String> nicknameByUserId = userExistenceRepository.findNicknamesByIds(userIds);
    Map<Long, Long> questionLikeCounts = Map.of();
    Set<Long> likedQuestionIds = Set.of();
    Map<Long, Long> replyLikeCounts = Map.of();
    Set<Long> likedQuestionReplyIds = Set.of();

    List<PersonalArtworkQuestionItemResult> questions =
        pageQuestions.stream()
            .map(
                question ->
                    toQuestionItem(
                        question,
                        replyByQuestionId.get(question.getPersonalQuestionId()),
                        nicknameByUserId,
                        ownerUserId,
                        query.userId(),
                        questionLikeCounts,
                        likedQuestionIds,
                        replyLikeCounts,
                        likedQuestionReplyIds))
            .toList();

    Long nextCursorId = hasNext ? questions.get(questions.size() - 1).personalQuestionId() : null;
    return new PersonalArtworkQuestionListResult(questions, nextCursorId, pageSize, hasNext);
  }

  private Map<Long, PersonalArtworkQuestionReply> findRepliesByQuestionId(
      List<PersonalArtworkQuestion> questions) {
    List<Long> questionIds =
        questions.stream().map(PersonalArtworkQuestion::getPersonalQuestionId).toList();
    return personalArtworkQuestionReplyRepository
        .findActiveByPersonalQuestionIds(questionIds)
        .stream()
        .collect(
            Collectors.toMap(
                PersonalArtworkQuestionReply::getPersonalQuestionId,
                Function.identity(),
                (first, ignored) -> first));
  }

  private Set<Long> collectUserIds(
      List<PersonalArtworkQuestion> questions,
      java.util.Collection<PersonalArtworkQuestionReply> replies) {
    Set<Long> userIds =
        questions.stream().map(PersonalArtworkQuestion::getUserId).collect(Collectors.toSet());
    replies.forEach(reply -> userIds.add(reply.getUserId()));
    return userIds;
  }

  private PersonalArtworkQuestionItemResult toQuestionItem(
      PersonalArtworkQuestion question,
      PersonalArtworkQuestionReply reply,
      Map<Long, String> nicknameByUserId,
      Long ownerUserId,
      Long userId,
      Map<Long, Long> questionLikeCounts,
      Set<Long> likedQuestionIds,
      Map<Long, Long> replyLikeCounts,
      Set<Long> likedQuestionReplyIds) {
    boolean isOwner = ownerUserId.equals(userId);
    boolean accessible =
        Boolean.TRUE.equals(question.getIsPublic()) || question.isWrittenBy(userId) || isOwner;
    boolean canReply = accessible && isOwner && !question.isAnswered();

    if (!accessible) {
      return new PersonalArtworkQuestionItemResult(
          question.getPersonalQuestionId(),
          null,
          question.getIsPublic(),
          false,
          false,
          false,
          null,
          false,
          question.getAnswerStatus(),
          question.getCreatedAt(),
          List.of(),
          null,
          null);
    }

    PersonalArtworkQuestionUserResult user =
        new PersonalArtworkQuestionUserResult(
            question.getUserId(),
            findNicknameOrThrow(nicknameByUserId, question.getUserId()),
            ownerUserId.equals(question.getUserId()));

    PersonalArtworkQuestionReplyItemResult replyResult =
        reply == null
            ? null
            : toReplyItem(
                reply,
                nicknameByUserId,
                ownerUserId,
                userId,
                replyLikeCounts,
                likedQuestionReplyIds);

    return new PersonalArtworkQuestionItemResult(
        question.getPersonalQuestionId(),
        question.getContent(),
        question.getIsPublic(),
        true,
        Objects.equals(question.getUserId(), userId),
        canReply,
        questionLikeCounts.getOrDefault(question.getPersonalQuestionId(), 0L),
        likedQuestionIds.contains(question.getPersonalQuestionId()),
        question.getAnswerStatus(),
        question.getCreatedAt(),
        question.getImages().stream()
            .map(
                image ->
                    new QuestionImageResult(
                        image.getPersonalQuestionImageId(),
                        image.getImageUrl(),
                        image.getWidth(),
                        image.getHeight(),
                        image.getSortOrder()))
            .toList(),
        user,
        replyResult);
  }

  private PersonalArtworkQuestionReplyItemResult toReplyItem(
      PersonalArtworkQuestionReply reply,
      Map<Long, String> nicknameByUserId,
      Long ownerUserId,
      Long userId,
      Map<Long, Long> replyLikeCounts,
      Set<Long> likedQuestionReplyIds) {
    return new PersonalArtworkQuestionReplyItemResult(
        reply.getPersonalQuestionReplyId(),
        reply.getUserId(),
        findNicknameOrThrow(nicknameByUserId, reply.getUserId()),
        ownerUserId.equals(reply.getUserId()),
        reply.getContent(),
        reply.getCreatedAt(),
        reply.getImages().stream()
            .map(
                image ->
                    new ReplyImageResult(
                        image.getPersonalQuestionReplyImageId(),
                        image.getImageUrl(),
                        image.getWidth(),
                        image.getHeight(),
                        image.getSortOrder()))
            .toList(),
        replyLikeCounts.getOrDefault(reply.getPersonalQuestionReplyId(), 0L),
        likedQuestionReplyIds.contains(reply.getPersonalQuestionReplyId()),
        Objects.equals(reply.getUserId(), userId));
  }

  private String findNicknameOrThrow(Map<Long, String> nicknameByUserId, Long userId) {
    String nickname = nicknameByUserId.get(userId);
    if (nickname == null) {
      throw new BusinessException(PersonalArtworkCommunicationErrorCode.USER_NOT_FOUND);
    }
    return nickname;
  }
}
