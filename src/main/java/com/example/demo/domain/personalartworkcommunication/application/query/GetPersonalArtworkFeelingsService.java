package com.example.demo.domain.personalartworkcommunication.application.query;

import com.example.demo.domain.personalartworkcommunication.application.command.PersonalArtworkFeelingValidator;
import com.example.demo.domain.personalartworkcommunication.application.result.PersonalArtworkFeelingListResult;
import com.example.demo.domain.personalartworkcommunication.application.result.PersonalArtworkFeelingListResult.ImageResult;
import com.example.demo.domain.personalartworkcommunication.application.result.PersonalArtworkFeelingListResult.PersonalArtworkFeelingItemResult;
import com.example.demo.domain.personalartworkcommunication.application.result.PersonalArtworkFeelingListResult.PersonalArtworkFeelingUserResult;
import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeeling;
import com.example.demo.domain.personalartworkcommunication.domain.error.PersonalArtworkCommunicationErrorCode;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkFeelingLikeRepository;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkFeelingReplyRepository;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkFeelingRepository;
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
public class GetPersonalArtworkFeelingsService {

  private static final int MAX_PAGE_SIZE = 50;

  private final PersonalArtworkFeelingValidator personalArtworkFeelingValidator;
  private final PersonalArtworkFeelingRepository personalArtworkFeelingRepository;
  private final PersonalArtworkFeelingReplyRepository personalArtworkFeelingReplyRepository;
  private final PersonalArtworkFeelingLikeRepository personalArtworkFeelingLikeRepository;
  private final UserExistenceRepository userExistenceRepository;

  @Transactional(readOnly = true)
  public PersonalArtworkFeelingListResult getFeelings(GetPersonalArtworkFeelingsQuery query) {
    Long ownerUserId =
        personalArtworkFeelingValidator
            .findPersonalArtworkAccessOrThrow(query.personalArtworkId())
            .ownerUserId();

    int pageSize = Math.min(Math.max(query.size(), 1), MAX_PAGE_SIZE);
    List<PersonalArtworkFeeling> fetched =
        personalArtworkFeelingRepository.findByPersonalArtworkIdWithCursorIncludingDeleted(
            query.personalArtworkId(), query.cursorId(), pageSize + 1);
    boolean hasNext = fetched.size() > pageSize;
    List<PersonalArtworkFeeling> pageFeelings = hasNext ? fetched.subList(0, pageSize) : fetched;

    if (pageFeelings.isEmpty()) {
      return new PersonalArtworkFeelingListResult(List.of(), null, pageSize, false);
    }

    List<Long> feelingIds =
        pageFeelings.stream().map(PersonalArtworkFeeling::getPersonalFeelingId).toList();
    Map<Long, Long> likeCounts =
        personalArtworkFeelingLikeRepository.countByPersonalFeelingIds(feelingIds);
    Set<Long> likedFeelingIds =
        query.viewerUserId() == null
            ? Set.of()
            : personalArtworkFeelingLikeRepository.findLikedPersonalFeelingIds(
                feelingIds, query.viewerUserId());
    Map<Long, Long> replyCounts =
        personalArtworkFeelingReplyRepository.countActiveByPersonalFeelingIds(feelingIds);
    Set<Long> userIds =
        pageFeelings.stream().map(PersonalArtworkFeeling::getUserId).collect(Collectors.toSet());
    Map<Long, UserProfile> userProfileById = userExistenceRepository.findUserProfilesByIds(userIds);

    List<PersonalArtworkFeelingItemResult> feelings =
        pageFeelings.stream()
            .map(
                feeling ->
                    toFeelingItem(
                        feeling,
                        userProfileById,
                        ownerUserId,
                        likeCounts,
                        likedFeelingIds,
                        query.viewerUserId(),
                        replyCounts))
            .toList();

    Long nextCursorId = hasNext ? feelings.get(feelings.size() - 1).personalFeelingId() : null;
    return new PersonalArtworkFeelingListResult(feelings, nextCursorId, pageSize, hasNext);
  }

  private PersonalArtworkFeelingItemResult toFeelingItem(
      PersonalArtworkFeeling feeling,
      Map<Long, UserProfile> userProfileById,
      Long ownerUserId,
      Map<Long, Long> likeCounts,
      Set<Long> likedFeelingIds,
      Long viewerUserId,
      Map<Long, Long> replyCounts) {
    UserProfile userProfile = findUserProfileOrThrow(userProfileById, feeling.getUserId());
    PersonalArtworkFeelingUserResult user =
        new PersonalArtworkFeelingUserResult(
            feeling.getUserId(),
            userProfile.nickname(),
            userProfile.profileImageUrl(),
            ownerUserId.equals(feeling.getUserId()));

    return new PersonalArtworkFeelingItemResult(
        feeling.getPersonalFeelingId(),
        feeling.getContent(),
        feeling.getCreatedAt(),
        feeling.isDeleted(),
        viewerUserId != null && feeling.isWrittenBy(viewerUserId),
        user,
        feeling.getImages().stream()
            .map(
                image ->
                    new ImageResult(
                        image.getPersonalFeelingImageId(),
                        image.getImageUrl(),
                        image.getWidth(),
                        image.getHeight(),
                        image.getSortOrder()))
            .toList(),
        likeCounts.getOrDefault(feeling.getPersonalFeelingId(), 0L),
        likedFeelingIds.contains(feeling.getPersonalFeelingId()),
        replyCounts.getOrDefault(feeling.getPersonalFeelingId(), 0L));
  }

  private UserProfile findUserProfileOrThrow(Map<Long, UserProfile> userProfileById, Long userId) {
    UserProfile userProfile = userProfileById.get(userId);
    if (userProfile == null) {
      throw new BusinessException(PersonalArtworkCommunicationErrorCode.USER_NOT_FOUND);
    }
    return userProfile;
  }
}
