package com.example.demo.domain.artworkcommunication.application.query;

import com.example.demo.domain.artworkcommunication.application.command.ArtworkFeelingValidator;
import com.example.demo.domain.artworkcommunication.application.query.ArtworkFeelingUserDisplayResolver.UserDisplayInfo;
import com.example.demo.domain.artworkcommunication.application.result.ArtworkFeelingReplyListResult;
import com.example.demo.domain.artworkcommunication.application.result.ArtworkFeelingReplyListResult.ArtworkFeelingReplyItemResult;
import com.example.demo.domain.artworkcommunication.application.result.ArtworkFeelingReplyListResult.ArtworkFeelingReplyUserResult;
import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkFeeling;
import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkFeelingReply;
import com.example.demo.domain.artworkcommunication.domain.error.ArtworkCommunicationErrorCode;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkFeelingReplyLikeRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkFeelingReplyRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkFeelingRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.CreatorExistenceRepository;
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
public class GetArtworkFeelingRepliesService {
  private static final int MAX_PAGE_SIZE = 50;

  private final ArtworkFeelingRepository artworkFeelingRepository;
  private final ArtworkFeelingReplyRepository artworkFeelingReplyRepository;
  private final ArtworkFeelingReplyLikeRepository artworkFeelingReplyLikeRepository;
  private final UserExistenceRepository userExistenceRepository;
  private final CreatorExistenceRepository creatorExistenceRepository;
  private final ArtworkFeelingValidator artworkFeelingValidator;
  private final ArtworkFeelingUserDisplayResolver userDisplayResolver;

  public ArtworkFeelingReplyListResult getReplies(GetArtworkFeelingRepliesQuery query) {
    artworkFeelingValidator.validateDisplayArtworkExists(query.displayArtworkId());
    ArtworkFeeling feeling =
        artworkFeelingRepository
            .findById(query.feelingId())
            .orElseThrow(
                () -> new BusinessException(ArtworkCommunicationErrorCode.FEELING_NOT_FOUND));
    artworkFeelingValidator.validateReplyListTarget(feeling, query.displayArtworkId());

    int pageSize = Math.min(Math.max(query.size(), 1), MAX_PAGE_SIZE);
    List<ArtworkFeelingReply> fetched =
        artworkFeelingReplyRepository.findActiveByFeelingIdWithCursor(
            query.feelingId(), query.cursorId(), pageSize + 1);
    boolean hasNext = fetched.size() > pageSize;
    List<ArtworkFeelingReply> replies = hasNext ? fetched.subList(0, pageSize) : fetched;
    if (replies.isEmpty()) {
      return new ArtworkFeelingReplyListResult(List.of(), null, pageSize, false);
    }

    List<Long> replyIds = replies.stream().map(ArtworkFeelingReply::getFeelingReplyId).toList();
    Map<Long, Long> likeCounts = artworkFeelingReplyLikeRepository.countByFeelingReplyIds(replyIds);
    Set<Long> likedReplyIds =
        query.viewerUserId() == null
            ? Set.of()
            : artworkFeelingReplyLikeRepository.findLikedFeelingReplyIds(
                replyIds, query.viewerUserId());
    Set<Long> userIds =
        replies.stream().map(ArtworkFeelingReply::getUserId).collect(Collectors.toSet());
    Map<Long, String> nicknameByUserId = userExistenceRepository.findNicknamesByIds(userIds);
    Map<Long, String> creatorNameByUserId =
        creatorExistenceRepository.findCreatorNamesByDisplayArtworkIdAndUserIds(
            query.displayArtworkId(), userIds);

    List<ArtworkFeelingReplyItemResult> items =
        replies.stream()
            .map(
                reply ->
                    new ArtworkFeelingReplyItemResult(
                        reply.getFeelingReplyId(),
                        reply.getContent(),
                        reply.getCreatedAt(),
                        toUserResult(
                            userDisplayResolver.resolve(
                                reply.getUserId(), nicknameByUserId, creatorNameByUserId)),
                        likeCounts.getOrDefault(reply.getFeelingReplyId(), 0L),
                        likedReplyIds.contains(reply.getFeelingReplyId())))
            .toList();

    Long nextCursorId = hasNext ? items.get(items.size() - 1).feelingReplyId() : null;
    return new ArtworkFeelingReplyListResult(items, nextCursorId, pageSize, hasNext);
  }

  private ArtworkFeelingReplyUserResult toUserResult(UserDisplayInfo user) {
    return new ArtworkFeelingReplyUserResult(user.userId(), user.nickname(), user.isCreator());
  }
}
