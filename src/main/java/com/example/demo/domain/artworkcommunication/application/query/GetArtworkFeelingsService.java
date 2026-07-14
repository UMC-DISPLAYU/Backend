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
import java.util.List;
import java.util.Map;
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

    List<ArtworkFeelingItemResult> feelings =
        pageFeelings.stream()
            .map(
                feeling ->
                    toFeelingItem(
                        query.displayArtworkId(),
                        feeling,
                        repliesByFeelingId.getOrDefault(feeling.getFeelingId(), List.of())))
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

  private ArtworkFeelingItemResult toFeelingItem(
      Long displayArtworkId, ArtworkFeeling feeling, List<ArtworkFeelingReply> replies) {
    ArtworkFeelingUserResult user =
        new ArtworkFeelingUserResult(
            feeling.getUserId(), findUserNicknameOrThrow(feeling.getUserId()));

    List<ArtworkFeelingReplyItemResult> replyResults =
        replies.stream().map(reply -> toReplyItem(displayArtworkId, reply)).toList();

    return new ArtworkFeelingItemResult(
        feeling.getFeelingId(), feeling.getContent(), feeling.getCreatedAt(), user, replyResults);
  }

  private ArtworkFeelingReplyItemResult toReplyItem(
      Long displayArtworkId, ArtworkFeelingReply reply) {
    // 답변 작성자가 해당 작품의 Creator이면 creatorName을, 아니면 회원 nickname을 표시한다.
    String creatorName =
        creatorExistenceRepository
            .findCreatorNameByDisplayArtworkIdAndUserId(displayArtworkId, reply.getUserId())
            .orElse(null);
    boolean isCreator = creatorName != null;
    String nickname = isCreator ? creatorName : findUserNicknameOrThrow(reply.getUserId());

    return new ArtworkFeelingReplyItemResult(
        reply.getUserId(), nickname, reply.getContent(), reply.getCreatedAt(), isCreator);
  }

  private String findUserNicknameOrThrow(Long userId) {
    return userExistenceRepository
        .findNicknameById(userId)
        .orElseThrow(() -> new BusinessException(ArtworkCommunicationErrorCode.USER_NOT_FOUND));
  }
}
