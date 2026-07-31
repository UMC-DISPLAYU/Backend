package com.example.demo.domain.personalartworkcommunication.application.query;

import com.example.demo.domain.personalartworkcommunication.application.result.PersonalArtworkFeelingListResult;
import com.example.demo.domain.personalartworkcommunication.application.result.PersonalArtworkFeelingListResult.ImageResult;
import com.example.demo.domain.personalartworkcommunication.application.result.PersonalArtworkFeelingListResult.PersonalArtworkFeelingItemResult;
import com.example.demo.domain.personalartworkcommunication.application.result.PersonalArtworkFeelingListResult.PersonalArtworkFeelingUserResult;
import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeeling;
import com.example.demo.domain.personalartworkcommunication.domain.error.PersonalArtworkCommunicationErrorCode;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkExistenceRepository;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkFeelingLikeRepository;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkFeelingReplyRepository;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkFeelingRepository;
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
public class GetPersonalArtworkFeelingsService {

  private static final int PAGE_SIZE = 3;

  private final PersonalArtworkExistenceRepository personalArtworkExistenceRepository;
  private final PersonalArtworkFeelingRepository personalArtworkFeelingRepository;
  private final PersonalArtworkFeelingReplyRepository personalArtworkFeelingReplyRepository;
  private final PersonalArtworkFeelingLikeRepository personalArtworkFeelingLikeRepository;
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

    List<Long> feelingIds =
        pageFeelings.stream().map(PersonalArtworkFeeling::getPersonalFeelingId).toList();
    Map<Long, Long> likeCounts =
        personalArtworkFeelingLikeRepository.countByPersonalFeelingIds(feelingIds);
    Map<Long, Long> replyCounts =
        personalArtworkFeelingReplyRepository.countActiveByPersonalFeelingIds(feelingIds);
    Set<Long> userIds =
        pageFeelings.stream().map(PersonalArtworkFeeling::getUserId).collect(Collectors.toSet());
    Map<Long, String> nicknameByUserId = userExistenceRepository.findNicknamesByIds(userIds);

    List<PersonalArtworkFeelingItemResult> feelings =
        pageFeelings.stream()
            .map(
                feeling ->
                    toFeelingItem(feeling, nicknameByUserId, ownerUserId, likeCounts, replyCounts))
            .toList();

    Long nextCursorId = hasNext ? feelings.get(feelings.size() - 1).personalFeelingId() : null;
    return new PersonalArtworkFeelingListResult(feelings, nextCursorId, PAGE_SIZE, hasNext);
  }

  private PersonalArtworkFeelingItemResult toFeelingItem(
      PersonalArtworkFeeling feeling,
      Map<Long, String> nicknameByUserId,
      Long ownerUserId,
      Map<Long, Long> likeCounts,
      Map<Long, Long> replyCounts) {
    PersonalArtworkFeelingUserResult user =
        new PersonalArtworkFeelingUserResult(
            feeling.getUserId(),
            findNicknameOrThrow(nicknameByUserId, feeling.getUserId()),
            ownerUserId.equals(feeling.getUserId()));

    return new PersonalArtworkFeelingItemResult(
        feeling.getPersonalFeelingId(),
        feeling.getContent(),
        feeling.getCreatedAt(),
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
        replyCounts.getOrDefault(feeling.getPersonalFeelingId(), 0L));
  }

  private String findNicknameOrThrow(Map<Long, String> nicknameByUserId, Long userId) {
    String nickname = nicknameByUserId.get(userId);
    if (nickname == null) {
      throw new BusinessException(PersonalArtworkCommunicationErrorCode.USER_NOT_FOUND);
    }
    return nickname;
  }
}
