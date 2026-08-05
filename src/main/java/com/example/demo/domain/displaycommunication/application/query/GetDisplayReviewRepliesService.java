package com.example.demo.domain.displaycommunication.application.query;

import com.example.demo.domain.displaycommunication.application.command.DisplayReviewValidator;
import com.example.demo.domain.displaycommunication.application.result.DisplayReviewReplyListResult;
import com.example.demo.domain.displaycommunication.application.result.DisplayReviewReplyListResult.ReplyItemResult;
import com.example.demo.domain.displaycommunication.application.result.DisplayReviewReplyListResult.UserResult;
import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReview;
import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReviewReply;
import com.example.demo.domain.displaycommunication.domain.error.DisplayCommunicationErrorCode;
import com.example.demo.domain.displaycommunication.domain.repository.DisplayReviewAccessRepository;
import com.example.demo.domain.displaycommunication.domain.repository.DisplayReviewAccessRepository.DisplayReviewAccess;
import com.example.demo.domain.displaycommunication.domain.repository.DisplayReviewReplyLikeRepository;
import com.example.demo.domain.displaycommunication.domain.repository.DisplayReviewReplyRepository;
import com.example.demo.domain.displaycommunication.domain.repository.UserExistenceRepository;
import com.example.demo.domain.displaycommunication.domain.repository.UserExistenceRepository.UserInfo;
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
public class GetDisplayReviewRepliesService {

  private final DisplayReviewValidator displayReviewValidator;
  private final DisplayReviewPagingPolicy pagingPolicy;
  private final DisplayReviewAccessRepository displayReviewAccessRepository;
  private final DisplayReviewReplyRepository displayReviewReplyRepository;
  private final DisplayReviewReplyLikeRepository displayReviewReplyLikeRepository;
  private final UserExistenceRepository userExistenceRepository;

  public DisplayReviewReplyListResult getReplies(GetDisplayReviewRepliesQuery query) {
    int pageSize = pagingPolicy.normalize(query.size());
    DisplayReviewAccess access = displayReviewValidator.findDisplayAccessOrThrow(query.displayId());
    DisplayReview review = displayReviewValidator.findReviewOrThrow(query.displayReviewId());
    displayReviewValidator.validateReviewTarget(review, query.displayId());

    List<DisplayReviewReply> fetched =
        displayReviewReplyRepository.findActiveByDisplayReviewIdWithCursor(
            query.displayReviewId(), query.cursorId(), pageSize + 1);
    boolean hasNext = fetched.size() > pageSize;
    List<DisplayReviewReply> replies = hasNext ? fetched.subList(0, pageSize) : fetched;
    if (replies.isEmpty()) {
      return new DisplayReviewReplyListResult(List.of(), null, pageSize, false);
    }

    List<Long> replyIds =
        replies.stream().map(DisplayReviewReply::getDisplayReviewReplyId).toList();
    Map<Long, Long> likeCounts =
        displayReviewReplyLikeRepository.countByDisplayReviewReplyIds(replyIds);
    Set<Long> likedReplyIds =
        query.viewerUserId() == null
            ? Set.of()
            : displayReviewReplyLikeRepository.findLikedDisplayReviewReplyIds(
                replyIds, query.viewerUserId());
    Set<Long> userIds =
        replies.stream().map(DisplayReviewReply::getUserId).collect(Collectors.toSet());
    Map<Long, UserInfo> users = userExistenceRepository.findUsersByIds(userIds);
    Set<Long> teamMemberUserIds =
        displayReviewAccessRepository.findAcceptedTeamMemberUserIds(query.displayId());

    List<ReplyItemResult> items =
        replies.stream()
            .map(
                reply ->
                    new ReplyItemResult(
                        reply.getDisplayReviewReplyId(),
                        reply.getContent(),
                        reply.getCreatedAt(),
                        toUserResult(users, reply.getUserId()),
                        access.ownerUserId().equals(reply.getUserId())
                            || teamMemberUserIds.contains(reply.getUserId()),
                        likeCounts.getOrDefault(reply.getDisplayReviewReplyId(), 0L),
                        likedReplyIds.contains(reply.getDisplayReviewReplyId())))
            .toList();

    Long nextCursorId = hasNext ? items.get(items.size() - 1).displayReviewReplyId() : null;
    return new DisplayReviewReplyListResult(items, nextCursorId, pageSize, hasNext);
  }

  private UserResult toUserResult(Map<Long, UserInfo> users, Long userId) {
    UserInfo user =
        java.util.Optional.ofNullable(users.get(userId))
            .orElseThrow(() -> new BusinessException(DisplayCommunicationErrorCode.USER_NOT_FOUND));
    return new UserResult(user.userId(), user.nickname(), user.profileImageUrl());
  }
}
