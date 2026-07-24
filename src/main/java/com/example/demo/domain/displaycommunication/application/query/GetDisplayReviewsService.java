package com.example.demo.domain.displaycommunication.application.query;

import com.example.demo.domain.display.domain.error.DisplayErrorCode;
import com.example.demo.domain.displaycommunication.application.result.DisplayReviewListResult;
import com.example.demo.domain.displaycommunication.application.result.DisplayReviewListResult.DisplayReviewItemResult;
import com.example.demo.domain.displaycommunication.application.result.DisplayReviewListResult.ImageResult;
import com.example.demo.domain.displaycommunication.application.result.DisplayReviewListResult.UserResult;
import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReview;
import com.example.demo.domain.displaycommunication.domain.error.DisplayCommunicationErrorCode;
import com.example.demo.domain.displaycommunication.domain.repository.DisplayExistenceRepository;
import com.example.demo.domain.displaycommunication.domain.repository.DisplayReviewLikeRepository;
import com.example.demo.domain.displaycommunication.domain.repository.DisplayReviewReplyRepository;
import com.example.demo.domain.displaycommunication.domain.repository.DisplayReviewRepository;
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
public class GetDisplayReviewsService {

  private static final int MAX_PAGE_SIZE = 50;

  private final DisplayExistenceRepository displayExistenceRepository;
  private final DisplayReviewRepository displayReviewRepository;
  private final DisplayReviewReplyRepository displayReviewReplyRepository;
  private final DisplayReviewLikeRepository displayReviewLikeRepository;
  private final UserExistenceRepository userExistenceRepository;

  public DisplayReviewListResult getReviews(GetDisplayReviewsQuery query) {
    int pageSize = Math.min(Math.max(query.size(), 1), MAX_PAGE_SIZE);
    if (!displayExistenceRepository.existsById(query.displayId())) {
      throw new BusinessException(DisplayErrorCode.DISPLAY_NOT_FOUND);
    }

    List<DisplayReview> fetched =
        displayReviewRepository.findActiveByDisplayIdWithCursor(
            query.displayId(), query.cursorId(), pageSize + 1);
    boolean hasNext = fetched.size() > pageSize;
    List<DisplayReview> reviews = hasNext ? fetched.subList(0, pageSize) : fetched;
    if (reviews.isEmpty()) {
      return new DisplayReviewListResult(List.of(), null, pageSize, false);
    }

    List<Long> reviewIds = reviews.stream().map(DisplayReview::getDisplayReviewId).toList();
    Map<Long, Long> reviewLikeCounts =
        displayReviewLikeRepository.countByDisplayReviewIds(reviewIds);
    Map<Long, Long> replyCounts =
        displayReviewReplyRepository.countActiveByDisplayReviewIds(reviewIds);
    Set<Long> userIds = reviews.stream().map(DisplayReview::getUserId).collect(Collectors.toSet());
    Map<Long, UserInfo> users = userExistenceRepository.findUsersByIds(userIds);

    List<DisplayReviewItemResult> items =
        reviews.stream()
            .map(review -> toReviewItem(review, users, reviewLikeCounts, replyCounts))
            .toList();

    Long nextCursorId = hasNext ? items.get(items.size() - 1).displayReviewId() : null;
    return new DisplayReviewListResult(items, nextCursorId, pageSize, hasNext);
  }

  private DisplayReviewItemResult toReviewItem(
      DisplayReview review,
      Map<Long, UserInfo> users,
      Map<Long, Long> reviewLikeCounts,
      Map<Long, Long> replyCounts) {
    List<ImageResult> images =
        review.getImages().stream()
            .map(
                image ->
                    new ImageResult(
                        image.getReviewImageId(),
                        image.getImageUrl(),
                        image.getWidth(),
                        image.getHeight(),
                        image.getSortOrder()))
            .toList();

    return new DisplayReviewItemResult(
        review.getDisplayReviewId(),
        review.getContent(),
        review.getCreatedAt(),
        toUserResult(users, review.getUserId()),
        images,
        reviewLikeCounts.getOrDefault(review.getDisplayReviewId(), 0L),
        replyCounts.getOrDefault(review.getDisplayReviewId(), 0L));
  }

  private UserResult toUserResult(Map<Long, UserInfo> users, Long userId) {
    UserInfo user =
        java.util.Optional.ofNullable(users.get(userId))
            .orElseThrow(() -> new BusinessException(DisplayCommunicationErrorCode.USER_NOT_FOUND));
    return new UserResult(user.userId(), user.nickname(), user.profileImageUrl());
  }
}
