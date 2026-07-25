package com.example.demo.domain.personalartworkcommunication.application.query;

import com.example.demo.domain.personalartworkcommunication.application.result.PersonalArtworkFeelingListResult;
import com.example.demo.domain.personalartworkcommunication.application.result.PersonalArtworkFeelingListResult.PersonalArtworkFeelingItemResult;
import com.example.demo.domain.personalartworkcommunication.application.result.PersonalArtworkFeelingListResult.PersonalArtworkFeelingReplyItemResult;
import com.example.demo.domain.personalartworkcommunication.application.result.PersonalArtworkFeelingListResult.PersonalArtworkFeelingUserResult;
import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeeling;
import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeelingReply;
import com.example.demo.domain.personalartworkcommunication.domain.error.PersonalArtworkCommunicationErrorCode;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkExistenceRepository;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkFeelingReplyRepository;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkFeelingRepository;
import com.example.demo.domain.personalartworkcommunication.domain.repository.UserExistenceRepository;
import com.example.demo.global.error.BusinessException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetPersonalArtworkFeelingsService {

  private static final int PAGE_SIZE = 3;

  private final PersonalArtworkExistenceRepository personalArtworkExistenceRepository;
  private final PersonalArtworkFeelingRepository personalArtworkFeelingRepository;
  private final PersonalArtworkFeelingReplyRepository personalArtworkFeelingReplyRepository;
  private final UserExistenceRepository userExistenceRepository;

  public PersonalArtworkFeelingListResult getFeelings(GetPersonalArtworkFeelingsQuery query) {
    Long ownerUserId =
        personalArtworkExistenceRepository
            .findOwnerUserIdById(query.personalArtworkId())
            .orElseThrow(
                () ->
                    new BusinessException(
                        PersonalArtworkCommunicationErrorCode.PERSONAL_ARTWORK_NOT_FOUND));

    List<PersonalArtworkFeeling> fetched =
        personalArtworkFeelingRepository.findActiveByPersonalArtworkIdWithCursor(
            query.personalArtworkId(), query.cursorId(), PAGE_SIZE + 1);
    boolean hasNext = fetched.size() > PAGE_SIZE;
    List<PersonalArtworkFeeling> pageFeelings = hasNext ? fetched.subList(0, PAGE_SIZE) : fetched;

    if (pageFeelings.isEmpty()) {
      return new PersonalArtworkFeelingListResult(List.of(), null, PAGE_SIZE, false);
    }

    Map<Long, List<PersonalArtworkFeelingReply>> repliesByFeelingId =
        findRepliesByFeelingId(pageFeelings);
    List<PersonalArtworkFeelingReply> replies =
        repliesByFeelingId.values().stream().flatMap(List::stream).toList();
    Set<Long> userIds = collectUserIds(pageFeelings, replies);
    Map<Long, String> nicknameByUserId = userExistenceRepository.findNicknamesByIds(userIds);

    List<PersonalArtworkFeelingItemResult> feelings =
        pageFeelings.stream()
            .map(
                feeling ->
                    toFeelingItem(
                        feeling,
                        repliesByFeelingId.getOrDefault(feeling.getPersonalFeelingId(), List.of()),
                        nicknameByUserId,
                        ownerUserId))
            .toList();

    Long nextCursorId = hasNext ? feelings.get(feelings.size() - 1).personalFeelingId() : null;
    return new PersonalArtworkFeelingListResult(feelings, nextCursorId, PAGE_SIZE, hasNext);
  }

  private Map<Long, List<PersonalArtworkFeelingReply>> findRepliesByFeelingId(
      List<PersonalArtworkFeeling> feelings) {
    List<Long> feelingIds =
        feelings.stream().map(PersonalArtworkFeeling::getPersonalFeelingId).toList();
    return personalArtworkFeelingReplyRepository.findActiveByPersonalFeelingIds(feelingIds).stream()
        .collect(Collectors.groupingBy(PersonalArtworkFeelingReply::getPersonalFeelingId));
  }

  private Set<Long> collectUserIds(
      List<PersonalArtworkFeeling> feelings, List<PersonalArtworkFeelingReply> replies) {
    Set<Long> userIds = new HashSet<>();
    feelings.forEach(feeling -> userIds.add(feeling.getUserId()));
    replies.forEach(reply -> userIds.add(reply.getUserId()));
    return userIds;
  }

  private PersonalArtworkFeelingItemResult toFeelingItem(
      PersonalArtworkFeeling feeling,
      List<PersonalArtworkFeelingReply> replies,
      Map<Long, String> nicknameByUserId,
      Long ownerUserId) {
    PersonalArtworkFeelingUserResult user =
        new PersonalArtworkFeelingUserResult(
            feeling.getUserId(),
            findNicknameOrThrow(nicknameByUserId, feeling.getUserId()),
            ownerUserId.equals(feeling.getUserId()));

    List<PersonalArtworkFeelingReplyItemResult> replyResults =
        replies.stream().map(reply -> toReplyItem(reply, nicknameByUserId, ownerUserId)).toList();

    return new PersonalArtworkFeelingItemResult(
        feeling.getPersonalFeelingId(),
        feeling.getContent(),
        feeling.getCreatedAt(),
        user,
        replyResults);
  }

  private PersonalArtworkFeelingReplyItemResult toReplyItem(
      PersonalArtworkFeelingReply reply, Map<Long, String> nicknameByUserId, Long ownerUserId) {
    return new PersonalArtworkFeelingReplyItemResult(
        reply.getPersonalFeelingReplyId(),
        reply.getUserId(),
        findNicknameOrThrow(nicknameByUserId, reply.getUserId()),
        reply.getContent(),
        reply.getCreatedAt(),
        ownerUserId.equals(reply.getUserId()));
  }

  private String findNicknameOrThrow(Map<Long, String> nicknameByUserId, Long userId) {
    String nickname = nicknameByUserId.get(userId);
    if (nickname == null) {
      throw new BusinessException(PersonalArtworkCommunicationErrorCode.USER_NOT_FOUND);
    }
    return nickname;
  }
}
