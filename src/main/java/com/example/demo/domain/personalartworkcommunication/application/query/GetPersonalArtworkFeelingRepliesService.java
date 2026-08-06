package com.example.demo.domain.personalartworkcommunication.application.query;

import com.example.demo.domain.personalartworkcommunication.application.command.PersonalArtworkFeelingValidator;
import com.example.demo.domain.personalartworkcommunication.application.result.PersonalArtworkFeelingReplyListResult;
import com.example.demo.domain.personalartworkcommunication.application.result.PersonalArtworkFeelingReplyListResult.PersonalArtworkFeelingReplyItemResult;
import com.example.demo.domain.personalartworkcommunication.application.result.PersonalArtworkFeelingReplyListResult.PersonalArtworkFeelingReplyUserResult;
import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeeling;
import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeelingReply;
import com.example.demo.domain.personalartworkcommunication.domain.error.PersonalArtworkCommunicationErrorCode;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkExistenceRepository;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkFeelingReplyLikeRepository;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkFeelingReplyRepository;
import com.example.demo.domain.personalartworkcommunication.domain.repository.UserExistenceRepository;
import com.example.demo.global.error.BusinessException;
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
public class GetPersonalArtworkFeelingRepliesService {
  private static final int MAX_PAGE_SIZE = 50;

  private final PersonalArtworkExistenceRepository personalArtworkExistenceRepository;
  private final PersonalArtworkFeelingReplyRepository personalArtworkFeelingReplyRepository;
  private final PersonalArtworkFeelingReplyLikeRepository personalArtworkFeelingReplyLikeRepository;
  private final UserExistenceRepository userExistenceRepository;
  private final PersonalArtworkFeelingValidator personalArtworkFeelingValidator;

  public PersonalArtworkFeelingReplyListResult getReplies(
      GetPersonalArtworkFeelingRepliesQuery query) {
    Long ownerUserId =
        personalArtworkExistenceRepository
            .findOwnerUserIdById(query.personalArtworkId())
            .orElseThrow(
                () ->
                    new BusinessException(
                        PersonalArtworkCommunicationErrorCode.PERSONAL_ARTWORK_NOT_FOUND));
    PersonalArtworkFeeling feeling =
        personalArtworkFeelingValidator.findFeelingOrThrow(query.personalFeelingId());
    personalArtworkFeelingValidator.validateReplyListTarget(feeling, query.personalArtworkId());

    int pageSize = Math.min(Math.max(query.size(), 1), MAX_PAGE_SIZE);
    List<PersonalArtworkFeelingReply> fetched =
        personalArtworkFeelingReplyRepository.findActiveByPersonalFeelingIdWithCursor(
            query.personalFeelingId(), query.cursorId(), pageSize + 1);
    boolean hasNext = fetched.size() > pageSize;
    List<PersonalArtworkFeelingReply> replies = hasNext ? fetched.subList(0, pageSize) : fetched;
    if (replies.isEmpty()) {
      return new PersonalArtworkFeelingReplyListResult(List.of(), null, pageSize, false);
    }

    List<Long> replyIds =
        replies.stream().map(PersonalArtworkFeelingReply::getPersonalFeelingReplyId).toList();
    Map<Long, Long> likeCounts =
        personalArtworkFeelingReplyLikeRepository.countByPersonalFeelingReplyIds(replyIds);
    Set<Long> likedReplyIds =
        query.viewerUserId() == null
            ? Set.of()
            : personalArtworkFeelingReplyLikeRepository.findLikedPersonalFeelingReplyIds(
                replyIds, query.viewerUserId());
    Set<Long> userIds =
        replies.stream().map(PersonalArtworkFeelingReply::getUserId).collect(Collectors.toSet());
    Map<Long, String> nicknameByUserId = userExistenceRepository.findNicknamesByIds(userIds);

    List<PersonalArtworkFeelingReplyItemResult> items =
        replies.stream()
            .map(
                reply ->
                    new PersonalArtworkFeelingReplyItemResult(
                        reply.getPersonalFeelingReplyId(),
                        reply.getContent(),
                        reply.getCreatedAt(),
                        new PersonalArtworkFeelingReplyUserResult(
                            reply.getUserId(),
                            findNicknameOrThrow(nicknameByUserId, reply.getUserId()),
                            ownerUserId.equals(reply.getUserId())),
                        likeCounts.getOrDefault(reply.getPersonalFeelingReplyId(), 0L),
                        likedReplyIds.contains(reply.getPersonalFeelingReplyId())))
            .toList();

    Long nextCursorId = hasNext ? items.get(items.size() - 1).personalFeelingReplyId() : null;
    return new PersonalArtworkFeelingReplyListResult(items, nextCursorId, pageSize, hasNext);
  }

  private String findNicknameOrThrow(Map<Long, String> nicknameByUserId, Long userId) {
    String nickname = nicknameByUserId.get(userId);
    if (nickname == null) {
      throw new BusinessException(PersonalArtworkCommunicationErrorCode.USER_NOT_FOUND);
    }
    return nickname;
  }
}
