package com.example.demo.domain.artworkcommunication.application.query;

import com.example.demo.domain.artworkcommunication.application.result.ArtworkFeelingListResult;
import com.example.demo.domain.artworkcommunication.application.result.ArtworkFeelingListResult.ArtworkFeelingItemResult;
import com.example.demo.domain.artworkcommunication.application.result.ArtworkFeelingListResult.ArtworkFeelingReplyItemResult;
import com.example.demo.domain.artworkcommunication.application.result.ArtworkFeelingListResult.ArtworkFeelingUserResult;
import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkFeeling;
import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkFeelingReply;
import com.example.demo.domain.artworkcommunication.domain.error.ArtworkCommunicationErrorCode;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkFeelingReplyRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkFeelingRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.CreatorExistenceRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.DisplayArtworkExistenceRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.UserExistenceRepository;
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
public class GetArtworkFeelingsService {
  private static final int PAGE_SIZE = 3;

  private final DisplayArtworkExistenceRepository displayArtworkExistenceRepository;
  private final ArtworkFeelingRepository artworkFeelingRepository;
  private final ArtworkFeelingReplyRepository artworkFeelingReplyRepository;
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
    List<ArtworkFeeling> pageFeelings = hasNext ? fetched.subList(0, PAGE_SIZE) : fetched;

    if (pageFeelings.isEmpty()) {
      return new ArtworkFeelingListResult(List.of(), null, PAGE_SIZE, false);
    }

    Map<Long, List<ArtworkFeelingReply>> repliesByFeelingId = findRepliesByFeelingId(pageFeelings);
    List<ArtworkFeelingReply> pageReplies =
        repliesByFeelingId.values().stream().flatMap(List::stream).toList();
    Set<Long> userIds = collectUserIds(pageFeelings, pageReplies);
    Map<Long, String> nicknameByUserId = userExistenceRepository.findNicknamesByIds(userIds);
    Map<Long, String> creatorNameByUserId =
        creatorExistenceRepository.findCreatorNamesByDisplayArtworkIdAndUserIds(
            query.displayArtworkId(), userIds);

    List<ArtworkFeelingItemResult> feelings =
        pageFeelings.stream()
            .map(
                feeling ->
                    toFeelingItem(
                        query.displayArtworkId(),
                        feeling,
                        repliesByFeelingId.getOrDefault(feeling.getFeelingId(), List.of()),
                        nicknameByUserId,
                        creatorNameByUserId))
            .toList();

    Long nextCursorId = hasNext ? feelings.get(feelings.size() - 1).feelingId() : null;
    return new ArtworkFeelingListResult(feelings, nextCursorId, PAGE_SIZE, hasNext);
  }

  private Map<Long, List<ArtworkFeelingReply>> findRepliesByFeelingId(
      List<ArtworkFeeling> feelings) {
    List<Long> feelingIds = feelings.stream().map(ArtworkFeeling::getFeelingId).toList();
    return artworkFeelingReplyRepository.findActiveByFeelingIds(feelingIds).stream()
        .collect(Collectors.groupingBy(ArtworkFeelingReply::getFeelingId));
  }

  private Set<Long> collectUserIds(
      List<ArtworkFeeling> feelings, List<ArtworkFeelingReply> replies) {
    Set<Long> userIds = new HashSet<>();
    feelings.forEach(feeling -> userIds.add(feeling.getUserId()));
    replies.forEach(reply -> userIds.add(reply.getUserId()));
    return userIds;
  }

  private ArtworkFeelingItemResult toFeelingItem(
      Long displayArtworkId,
      ArtworkFeeling feeling,
      List<ArtworkFeelingReply> replies,
      Map<Long, String> nicknameByUserId,
      Map<Long, String> creatorNameByUserId) {
    ArtworkFeelingUserResult user =
        new ArtworkFeelingUserResult(
            feeling.getUserId(), findNicknameOrThrow(nicknameByUserId, feeling.getUserId()));

    List<ArtworkFeelingReplyItemResult> replyResults =
        replies.stream()
            .map(reply -> toReplyItem(reply, nicknameByUserId, creatorNameByUserId))
            .toList();

    return new ArtworkFeelingItemResult(
        feeling.getFeelingId(), feeling.getContent(), feeling.getCreatedAt(), user, replyResults);
  }

  private ArtworkFeelingReplyItemResult toReplyItem(
      ArtworkFeelingReply reply,
      Map<Long, String> nicknameByUserId,
      Map<Long, String> creatorNameByUserId) {
    String creatorName = creatorNameByUserId.get(reply.getUserId());
    boolean isCreator = creatorName != null;
    String nickname =
        isCreator ? creatorName : findNicknameOrThrow(nicknameByUserId, reply.getUserId());

    return new ArtworkFeelingReplyItemResult(
        reply.getUserId(), nickname, reply.getContent(), reply.getCreatedAt(), isCreator);
  }

  private String findNicknameOrThrow(Map<Long, String> nicknameByUserId, Long userId) {
    String nickname = nicknameByUserId.get(userId);
    if (nickname == null) {
      throw new BusinessException(ArtworkCommunicationErrorCode.USER_NOT_FOUND);
    }
    return nickname;
  }
}
