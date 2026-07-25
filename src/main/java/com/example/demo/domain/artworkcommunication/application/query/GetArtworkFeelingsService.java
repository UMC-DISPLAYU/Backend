package com.example.demo.domain.artworkcommunication.application.query;

import com.example.demo.domain.artworkcommunication.application.result.ArtworkFeelingListResult;
import com.example.demo.domain.artworkcommunication.application.result.ArtworkFeelingListResult.ArtworkFeelingItemResult;
import com.example.demo.domain.artworkcommunication.application.result.ArtworkFeelingListResult.ArtworkFeelingUserResult;
import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkFeeling;
import com.example.demo.domain.artworkcommunication.domain.error.ArtworkCommunicationErrorCode;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkFeelingLikeRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkFeelingReplyRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkFeelingRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.CreatorExistenceRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.DisplayArtworkExistenceRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.UserExistenceRepository;
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
public class GetArtworkFeelingsService {
  private static final int PAGE_SIZE = 3;

  private final DisplayArtworkExistenceRepository displayArtworkExistenceRepository;
  private final ArtworkFeelingRepository artworkFeelingRepository;
  private final ArtworkFeelingReplyRepository artworkFeelingReplyRepository;
  private final ArtworkFeelingLikeRepository artworkFeelingLikeRepository;
  private final UserExistenceRepository userExistenceRepository;
  private final CreatorExistenceRepository creatorExistenceRepository;

  public ArtworkFeelingListResult getFeelings(GetArtworkFeelingsQuery query) {
    if (!displayArtworkExistenceRepository.existsById(query.displayArtworkId())) {
      throw new BusinessException(ArtworkCommunicationErrorCode.ARTWORK_NOT_FOUND);
    }

    List<ArtworkFeeling> fetched =
        artworkFeelingRepository.findActiveByDisplayArtworkIdWithCursor(
            query.displayArtworkId(), query.cursorId(), PAGE_SIZE + 1);
    boolean hasNext = fetched.size() > PAGE_SIZE;
    List<ArtworkFeeling> feelings = hasNext ? fetched.subList(0, PAGE_SIZE) : fetched;
    if (feelings.isEmpty()) {
      return new ArtworkFeelingListResult(List.of(), null, PAGE_SIZE, false);
    }

    List<Long> feelingIds = feelings.stream().map(ArtworkFeeling::getFeelingId).toList();
    Map<Long, Long> likeCounts = artworkFeelingLikeRepository.countByFeelingIds(feelingIds);
    Map<Long, Long> replyCounts = artworkFeelingReplyRepository.countActiveByFeelingIds(feelingIds);
    Set<Long> userIds =
        feelings.stream().map(ArtworkFeeling::getUserId).collect(Collectors.toSet());
    Map<Long, String> nicknameByUserId = userExistenceRepository.findNicknamesByIds(userIds);
    Map<Long, String> creatorNameByUserId =
        creatorExistenceRepository.findCreatorNamesByDisplayArtworkIdAndUserIds(
            query.displayArtworkId(), userIds);

    List<ArtworkFeelingItemResult> items =
        feelings.stream()
            .map(
                feeling ->
                    new ArtworkFeelingItemResult(
                        feeling.getFeelingId(),
                        feeling.getContent(),
                        feeling.getCreatedAt(),
                        toUserResult(feeling.getUserId(), nicknameByUserId, creatorNameByUserId),
                        likeCounts.getOrDefault(feeling.getFeelingId(), 0L),
                        replyCounts.getOrDefault(feeling.getFeelingId(), 0L)))
            .toList();

    Long nextCursorId = hasNext ? items.get(items.size() - 1).feelingId() : null;
    return new ArtworkFeelingListResult(items, nextCursorId, PAGE_SIZE, hasNext);
  }

  private ArtworkFeelingUserResult toUserResult(
      Long userId, Map<Long, String> nicknameByUserId, Map<Long, String> creatorNameByUserId) {
    String creatorName = creatorNameByUserId.get(userId);
    boolean isCreator = creatorName != null;
    String nickname = isCreator ? creatorName : findNicknameOrThrow(nicknameByUserId, userId);
    return new ArtworkFeelingUserResult(userId, nickname, isCreator);
  }

  private String findNicknameOrThrow(Map<Long, String> nicknameByUserId, Long userId) {
    String nickname = nicknameByUserId.get(userId);
    if (nickname == null) {
      throw new BusinessException(ArtworkCommunicationErrorCode.USER_NOT_FOUND);
    }
    return nickname;
  }
}
