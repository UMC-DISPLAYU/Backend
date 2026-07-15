package com.example.demo.domain.lounge.application.query;

import com.example.demo.domain.lounge.application.result.LoungeCommentCursorResult;
import com.example.demo.domain.lounge.application.result.LoungeCommentListResult;
import com.example.demo.domain.lounge.application.result.LoungeReplyCursorResult;
import com.example.demo.domain.lounge.application.result.WriterView;
import com.example.demo.domain.lounge.domain.aggregate.LoungePost;
import com.example.demo.domain.lounge.domain.entity.LoungeComment;
import com.example.demo.domain.lounge.domain.error.LoungeErrorCode;
import com.example.demo.domain.lounge.domain.repository.LoungeCommentLikeRepository;
import com.example.demo.domain.lounge.domain.repository.LoungeCommentRepository;
import com.example.demo.domain.lounge.domain.repository.LoungePostRepository;
import com.example.demo.domain.lounge.domain.repository.LoungeWriterRepository;
import com.example.demo.domain.lounge.domain.vo.LoungeWriter;
import com.example.demo.domain.lounge.domain.vo.UserId;
import com.example.demo.global.error.BusinessException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoungeCommentQueryService {
  private static final int MAX_PAGE_SIZE = 50;

  private final LoungePostRepository loungePostRepository;
  private final LoungeCommentRepository loungeCommentRepository;
  private final LoungeCommentQueryRepository loungeCommentQueryRepository;
  private final LoungeCommentLikeRepository loungeCommentLikeRepository;
  private final LoungeWriterRepository loungeWriterRepository;

  public LoungeCommentQueryService(
      LoungePostRepository loungePostRepository,
      LoungeCommentRepository loungeCommentRepository,
      LoungeCommentQueryRepository loungeCommentQueryRepository,
      LoungeCommentLikeRepository loungeCommentLikeRepository,
      LoungeWriterRepository loungeWriterRepository) {
    this.loungePostRepository = loungePostRepository;
    this.loungeCommentRepository = loungeCommentRepository;
    this.loungeCommentQueryRepository = loungeCommentQueryRepository;
    this.loungeCommentLikeRepository = loungeCommentLikeRepository;
    this.loungeWriterRepository = loungeWriterRepository;
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

  @Transactional(readOnly = true)
  public LoungeReplyCursorResult getReplies(
      Long parentCommentId, Long cursorId, int size, Long viewerUserId) {
    LoungeComment parentComment = getActiveComment(parentCommentId);
    if (!parentComment.isRootComment()) {
      throw new BusinessException(LoungeErrorCode.INVALID_REPLY_TARGET);
    }

    int pageSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
    List<LoungeCommentQueryResult> fetched =
        loungeCommentQueryRepository.findActiveRepliesByCursor(
            parentComment.getId(), cursorId, pageSize + 1);
    boolean hasNext = fetched.size() > pageSize;
    List<LoungeCommentQueryResult> replies = hasNext ? fetched.subList(0, pageSize) : fetched;
    if (replies.isEmpty()) {
      return new LoungeReplyCursorResult(List.of(), null, pageSize, false);
    }

    List<LoungeCommentListResult> results = toResults(replies, viewerUserId, false);
    Long nextCursorId = hasNext ? results.get(results.size() - 1).loungeCommentId() : null;
    return new LoungeReplyCursorResult(results, nextCursorId, pageSize, hasNext);
  }

  private List<LoungeCommentListResult> toResults(
      List<LoungeCommentQueryResult> comments, Long viewerUserId, boolean includeReplyCount) {
    List<Long> commentIds =
        comments.stream().map(LoungeCommentQueryResult::loungeCommentId).toList();
    Map<Long, Long> likeCounts = loungeCommentLikeRepository.countByLoungeCommentIds(commentIds);
    Map<Long, Long> replyCounts =
        includeReplyCount
            ? loungeCommentRepository.countActiveRepliesByParentCommentIds(commentIds)
            : Map.of();
    Set<Long> likedCommentIds =
        viewerUserId == null
            ? Set.of()
            : loungeCommentLikeRepository.findLikedLoungeCommentIds(
                commentIds, new UserId(viewerUserId));
    Map<Long, LoungeWriter> writers =
        loungeWriterRepository.findByUserIds(
            comments.stream().map(LoungeCommentQueryResult::authorUserId).distinct().toList());

    return comments.stream()
        .map(
            comment ->
                LoungeCommentListResult.from(
                    comment,
                    toWriterView(
                        writers.getOrDefault(
                            comment.authorUserId(), LoungeWriter.unknown(comment.authorUserId()))),
                    likeCounts.getOrDefault(comment.loungeCommentId(), 0L),
                    replyCounts.getOrDefault(comment.loungeCommentId(), 0L),
                    likedCommentIds.contains(comment.loungeCommentId()),
                    viewerUserId))
        .toList();
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

  private LoungeComment getActiveComment(Long loungeCommentId) {
    return loungeCommentRepository
        .findById(loungeCommentId)
        .filter(comment -> !comment.isDeleted())
        .filter(LoungeComment::isActive)
        .orElseThrow(() -> new BusinessException(LoungeErrorCode.LOUNGE_COMMENT_NOT_FOUND));
  }
}
