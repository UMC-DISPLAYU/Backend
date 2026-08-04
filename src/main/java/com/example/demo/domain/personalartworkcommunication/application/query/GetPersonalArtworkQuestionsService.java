package com.example.demo.domain.personalartworkcommunication.application.query;

import com.example.demo.domain.personalartworkcommunication.application.command.PersonalArtworkQuestionValidator;
import com.example.demo.domain.personalartworkcommunication.application.result.PersonalArtworkQuestionListResult;
import com.example.demo.domain.personalartworkcommunication.application.result.PersonalArtworkQuestionListResult.PersonalArtworkQuestionItemResult;
import com.example.demo.domain.personalartworkcommunication.application.result.PersonalArtworkQuestionListResult.PersonalArtworkQuestionReplyItemResult;
import com.example.demo.domain.personalartworkcommunication.application.result.PersonalArtworkQuestionListResult.PersonalArtworkQuestionUserResult;
import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkQuestion;
import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkQuestionReply;
import com.example.demo.domain.personalartworkcommunication.domain.error.PersonalArtworkCommunicationErrorCode;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkExistenceRepository;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkQuestionLikeRepository;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkQuestionReplyLikeRepository;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkQuestionReplyRepository;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkQuestionRepository;
import com.example.demo.domain.personalartworkcommunication.domain.repository.UserExistenceRepository;
import com.example.demo.global.error.BusinessException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetPersonalArtworkQuestionsService {

  private static final int PAGE_SIZE = 3;

  private final PersonalArtworkQuestionRepository personalArtworkQuestionRepository;
  private final PersonalArtworkQuestionReplyRepository personalArtworkQuestionReplyRepository;
  private final PersonalArtworkQuestionLikeRepository personalArtworkQuestionLikeRepository;
  private final PersonalArtworkQuestionReplyLikeRepository
      personalArtworkQuestionReplyLikeRepository;
  private final PersonalArtworkExistenceRepository personalArtworkExistenceRepository;
  private final UserExistenceRepository userExistenceRepository;
  private final PersonalArtworkQuestionValidator personalArtworkQuestionValidator;

  public PersonalArtworkQuestionListResult getQuestions(GetPersonalArtworkQuestionsQuery query) {
    personalArtworkQuestionValidator.validatePersonalArtworkExists(query.personalArtworkId());
    Long ownerUserId =
        personalArtworkExistenceRepository
            .findOwnerUserIdById(query.personalArtworkId())
            .orElseThrow(
                () ->
                    new BusinessException(
                        PersonalArtworkCommunicationErrorCode.PERSONAL_ARTWORK_NOT_FOUND));

    List<PersonalArtworkQuestion> fetched =
        personalArtworkQuestionRepository.findActiveByPersonalArtworkIdWithCursor(
            query.personalArtworkId(), query.cursorId(), PAGE_SIZE + 1);
    boolean hasNext = fetched.size() > PAGE_SIZE;
    List<PersonalArtworkQuestion> pageQuestions = hasNext ? fetched.subList(0, PAGE_SIZE) : fetched;

    if (pageQuestions.isEmpty()) {
      return new PersonalArtworkQuestionListResult(List.of(), null, PAGE_SIZE, false);
    }

    Map<Long, PersonalArtworkQuestionReply> replyByQuestionId =
        findRepliesByQuestionId(pageQuestions);
    Set<Long> userIds = collectUserIds(pageQuestions, replyByQuestionId.values());
    Map<Long, String> nicknameByUserId = userExistenceRepository.findNicknamesByIds(userIds);
    Map<Long, Long> questionLikeCounts =
        personalArtworkQuestionLikeRepository.countByPersonalQuestionIds(
            pageQuestions.stream().map(PersonalArtworkQuestion::getPersonalQuestionId).toList());
    Map<Long, Long> replyLikeCounts =
        personalArtworkQuestionReplyLikeRepository.countByPersonalQuestionReplyIds(
            replyByQuestionId.values().stream()
                .map(PersonalArtworkQuestionReply::getPersonalQuestionReplyId)
                .toList());

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
                        replyLikeCounts))
            .toList();

    Long nextCursorId = hasNext ? questions.get(questions.size() - 1).personalQuestionId() : null;
    return new PersonalArtworkQuestionListResult(questions, nextCursorId, PAGE_SIZE, hasNext);
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
      Map<Long, Long> replyLikeCounts) {
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
          null,
          question.getAnswerStatus(),
          question.getCreatedAt(),
          null,
          null);
    }

    PersonalArtworkQuestionUserResult user =
        new PersonalArtworkQuestionUserResult(
            question.getUserId(), findNicknameOrThrow(nicknameByUserId, question.getUserId()));

    PersonalArtworkQuestionReplyItemResult replyResult =
        reply == null ? null : toReplyItem(reply, nicknameByUserId, ownerUserId, replyLikeCounts);

    return new PersonalArtworkQuestionItemResult(
        question.getPersonalQuestionId(),
        question.getContent(),
        question.getIsPublic(),
        true,
        canReply,
        questionLikeCounts.getOrDefault(question.getPersonalQuestionId(), 0L),
        question.getAnswerStatus(),
        question.getCreatedAt(),
        user,
        replyResult);
  }

  private PersonalArtworkQuestionReplyItemResult toReplyItem(
      PersonalArtworkQuestionReply reply,
      Map<Long, String> nicknameByUserId,
      Long ownerUserId,
      Map<Long, Long> replyLikeCounts) {
    return new PersonalArtworkQuestionReplyItemResult(
        reply.getPersonalQuestionReplyId(),
        reply.getUserId(),
        findNicknameOrThrow(nicknameByUserId, reply.getUserId()),
        ownerUserId.equals(reply.getUserId()),
        reply.getContent(),
        reply.getCreatedAt(),
        replyLikeCounts.getOrDefault(reply.getPersonalQuestionReplyId(), 0L));
  }

  private String findNicknameOrThrow(Map<Long, String> nicknameByUserId, Long userId) {
    String nickname = nicknameByUserId.get(userId);
    if (nickname == null) {
      throw new BusinessException(PersonalArtworkCommunicationErrorCode.USER_NOT_FOUND);
    }
    return nickname;
  }
}
