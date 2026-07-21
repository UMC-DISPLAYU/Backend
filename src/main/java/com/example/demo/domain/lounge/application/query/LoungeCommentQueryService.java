package com.example.demo.domain.lounge.application.query;

import com.example.demo.domain.lounge.application.result.LoungeCommentCursorResult;
import com.example.demo.domain.lounge.application.result.LoungeCommentListResult;
import com.example.demo.domain.lounge.application.result.WriterView;
import com.example.demo.domain.lounge.domain.aggregate.LoungePost;
import com.example.demo.domain.lounge.domain.error.LoungeErrorCode;
import com.example.demo.domain.lounge.domain.repository.LoungeCommentLikeRepository;
import com.example.demo.domain.lounge.domain.repository.LoungePostRepository;
import com.example.demo.domain.lounge.domain.repository.LoungeWriterRepository;
import com.example.demo.domain.lounge.domain.vo.LoungeWriter;
import com.example.demo.domain.lounge.domain.vo.UserId;
import com.example.demo.global.error.BusinessException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoungeCommentQueryService {
  private static final int MAX_PAGE_SIZE = 50;

  private final LoungePostRepository loungePostRepository;
  private final LoungeCommentQueryRepository loungeCommentQueryRepository;
  private final LoungeCommentLikeRepository loungeCommentLikeRepository;
  private final LoungeWriterRepository loungeWriterRepository;

  public LoungeCommentQueryService(
      LoungePostRepository loungePostRepository,
      LoungeCommentQueryRepository loungeCommentQueryRepository,
      LoungeCommentLikeRepository loungeCommentLikeRepository,
      LoungeWriterRepository loungeWriterRepository) {
    this.loungePostRepository = loungePostRepository;
    this.loungeCommentQueryRepository = loungeCommentQueryRepository;
    this.loungeCommentLikeRepository = loungeCommentLikeRepository;
    this.loungeWriterRepository = loungeWriterRepository;
  }

  @Transactional(readOnly = true)
  public LoungeCommentListResult getComment(Long loungeCommentId, Long viewerUserId) {
    LoungeCommentQueryResult comment =
        loungeCommentQueryRepository
            .findActiveById(loungeCommentId)
            .orElseThrow(() -> new BusinessException(LoungeErrorCode.LOUNGE_COMMENT_NOT_FOUND));

    return toResults(List.of(comment), viewerUserId, comment.parentCommentId() == null).getFirst();
  }

  @Transactional(readOnly = true)
  public LoungeCommentCursorResult getComments(
      Long loungePostId, Long cursorId, int size, Long viewerUserId) {
    LoungePost loungePost = getActivePost(loungePostId);
    int pageSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
    List<LoungeCommentQueryResult> fetched =
        loungeCommentQueryRepository.findActiveRootByCursor(
            loungePost.getId(), cursorId, pageSize + 1);
    boolean hasNext = fetched.size() > pageSize;
    List<LoungeCommentQueryResult> comments = hasNext ? fetched.subList(0, pageSize) : fetched;
    if (comments.isEmpty()) {
      return new LoungeCommentCursorResult(List.of(), null, pageSize, false);
    }

    List<LoungeCommentListResult> results = toResults(comments, viewerUserId, true);
    Long nextCursorId = hasNext ? results.get(results.size() - 1).loungeCommentId() : null;
    return new LoungeCommentCursorResult(results, nextCursorId, pageSize, hasNext);
  }

  private List<LoungeCommentListResult> toResults(
      List<LoungeCommentQueryResult> comments, Long viewerUserId, boolean includeReplies) {
    List<Long> commentIds =
        comments.stream().map(LoungeCommentQueryResult::loungeCommentId).toList();
    List<LoungeCommentQueryResult> replies =
        includeReplies
            ? loungeCommentQueryRepository.findActiveRepliesByParentCommentIds(commentIds)
            : List.of();
    List<LoungeCommentQueryResult> allComments =
        Stream.concat(comments.stream(), replies.stream()).toList();
    List<Long> allCommentIds =
        allComments.stream().map(LoungeCommentQueryResult::loungeCommentId).toList();
    Map<Long, Long> likeCounts = loungeCommentLikeRepository.countByLoungeCommentIds(allCommentIds);
    Set<Long> likedCommentIds =
        viewerUserId == null
            ? Set.of()
            : loungeCommentLikeRepository.findLikedLoungeCommentIds(
                allCommentIds, new UserId(viewerUserId));
    Map<Long, LoungeWriter> writers =
        loungeWriterRepository.findByUserIds(
            allComments.stream().map(LoungeCommentQueryResult::authorUserId).distinct().toList());
    Map<Long, List<LoungeCommentListResult>> repliesByParentId =
        replies.stream()
            .map(
                reply ->
                    toResult(reply, viewerUserId, likeCounts, likedCommentIds, writers, List.of()))
            .collect(Collectors.groupingBy(LoungeCommentListResult::parentCommentId));

    return comments.stream()
        .map(
            comment ->
                toResult(
                    comment,
                    viewerUserId,
                    likeCounts,
                    likedCommentIds,
                    writers,
                    repliesByParentId.getOrDefault(comment.loungeCommentId(), List.of())))
        .toList();
  }

  private LoungeCommentListResult toResult(
      LoungeCommentQueryResult comment,
      Long viewerUserId,
      Map<Long, Long> likeCounts,
      Set<Long> likedCommentIds,
      Map<Long, LoungeWriter> writers,
      List<LoungeCommentListResult> replies) {
    return LoungeCommentListResult.from(
        comment,
        toWriterView(
            writers.getOrDefault(
                comment.authorUserId(), LoungeWriter.unknown(comment.authorUserId()))),
        likeCounts.getOrDefault(comment.loungeCommentId(), 0L),
        replies.size(),
        likedCommentIds.contains(comment.loungeCommentId()),
        viewerUserId,
        replies);
  }

  private WriterView toWriterView(LoungeWriter writer) {
    return new WriterView(writer.userId(), writer.nickname(), writer.profileImageUrl());
  }

  private LoungePost getActivePost(Long loungePostId) {
    return loungePostRepository
        .findById(loungePostId)
        .filter(post -> !post.isDeleted())
        .filter(LoungePost::isActive)
        .orElseThrow(() -> new BusinessException(LoungeErrorCode.LOUNGE_POST_NOT_FOUND));
  }
}
