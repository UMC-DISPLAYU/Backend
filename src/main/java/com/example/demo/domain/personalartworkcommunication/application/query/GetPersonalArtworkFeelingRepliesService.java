package com.example.demo.domain.personalartworkcommunication.application.query;

import com.example.demo.domain.personalartworkcommunication.application.command.PersonalArtworkFeelingValidator;
import com.example.demo.domain.personalartworkcommunication.application.result.PersonalArtworkFeelingReplyListResult;
import com.example.demo.domain.personalartworkcommunication.application.result.PersonalArtworkFeelingReplyListResult.ImageResult;
import com.example.demo.domain.personalartworkcommunication.application.result.PersonalArtworkFeelingReplyListResult.PersonalArtworkFeelingReplyItemResult;
import com.example.demo.domain.personalartworkcommunication.application.result.PersonalArtworkFeelingReplyListResult.PersonalArtworkFeelingReplyUserResult;
import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeeling;
import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeelingReply;
import com.example.demo.domain.personalartworkcommunication.domain.error.PersonalArtworkCommunicationErrorCode;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkFeelingReplyLikeRepository;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkFeelingReplyRepository;
import com.example.demo.domain.personalartworkcommunication.domain.repository.UserExistenceRepository;
import com.example.demo.domain.personalartworkcommunication.domain.repository.UserExistenceRepository.UserProfile;
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
public class GetPersonalArtworkFeelingRepliesService {
  private static final int MAX_PAGE_SIZE = 50;

  private final PersonalArtworkFeelingReplyRepository personalArtworkFeelingReplyRepository;
  private final PersonalArtworkFeelingReplyLikeRepository personalArtworkFeelingReplyLikeRepository;
  private final UserExistenceRepository userExistenceRepository;
  private final PersonalArtworkFeelingValidator personalArtworkFeelingValidator;

  @Transactional(readOnly = true)
  public PersonalArtworkFeelingReplyListResult getReplies(
      GetPersonalArtworkFeelingRepliesQuery query) {
    Long ownerUserId =
        personalArtworkFeelingValidator
            .findPersonalArtworkAccessOrThrow(query.personalArtworkId())
            .ownerUserId();
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
    Map<Long, UserProfile> userProfileById = userExistenceRepository.findUserProfilesByIds(userIds);

    List<PersonalArtworkFeelingReplyItemResult> items =
        replies.stream()
            .map(
                reply ->
                    new PersonalArtworkFeelingReplyItemResult(
                        reply.getPersonalFeelingReplyId(),
                        reply.getContent(),
                        reply.getCreatedAt(),
                        toUserResult(reply.getUserId(), userProfileById, ownerUserId),
                        likeCounts.getOrDefault(reply.getPersonalFeelingReplyId(), 0L),
                        likedReplyIds.contains(reply.getPersonalFeelingReplyId()),
                        reply.getImages().stream()
                            .map(
                                image ->
                                    new ImageResult(
                                        image.getPersonalFeelingReplyImageId(),
                                        image.getImageUrl(),
                                        image.getWidth(),
                                        image.getHeight(),
                                        image.getSortOrder()))
                            .toList()))
            .toList();

    Long nextCursorId = hasNext ? items.get(items.size() - 1).personalFeelingReplyId() : null;
    return new PersonalArtworkFeelingReplyListResult(items, nextCursorId, pageSize, hasNext);
  }

  private PersonalArtworkFeelingReplyUserResult toUserResult(
      Long userId, Map<Long, UserProfile> userProfileById, Long ownerUserId) {
    UserProfile userProfile = userProfileById.get(userId);
    if (userProfile == null) {
      throw new BusinessException(PersonalArtworkCommunicationErrorCode.USER_NOT_FOUND);
    }
    return new PersonalArtworkFeelingReplyUserResult(
        userId, userProfile.nickname(), userProfile.profileImageUrl(), ownerUserId.equals(userId));
  }
}
